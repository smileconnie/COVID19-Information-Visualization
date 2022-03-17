package com.xjy.process

import com.alibaba.fastjson.{JSON, JSONObject}
import com.xjy.util.OffsetUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import java.sql.{Connection, DriverManager, PreparedStatement}
import scala.collection.mutable

/**
 * @Author ：xjy
 * @Desc ：疫情物资数据的实时处理与分析
 */
object Covid19_MaterialData_Process {
  def main(args: Array[String]): Unit = {
    //1.准备SparkStreaming的开发环境
    val conf: SparkConf = new SparkConf().setAppName("Covid19_MaterialData_Process").setMaster("local[*]")
    val sc: SparkContext = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(5))
    ssc.checkpoint("./sscckp")

    //补充知识点：SparkStreaming整合kafka的两种方式：
    //1.Receiver模式
    //KafkaUtils.creatDStream--API创建
    //会有一个Receiver作为常驻Task运行在Executor进程中，一直等待数据的到来
    //一个Receiver效率会比较低，那么可以使用多个Receiver，但是多个Receiver中的数据又需要手动进行Union(合并)
    //且其中某个Receiver挂了，会导致数据丢失，需要开启WAL预写日志来保证数据安全，但是效率又低了
    //Receiver模式使用Zookeeper来连接Kafka(Kafka的新版本中已经不推荐使用该方式)
    //Receiver模式使用的是Kafka的高阶API(高度封装的)，offset由Receiver提交到ZK中(Kafka的新版本中offset默认存储在默认主题
    // __consumer__offset中的，不推荐存入到ZK中了)，容易和Spark维护在Checkpoint中的offset不一致
    //所以不管从何种角度去说Receiver模式都已经不再适合现如今的Kafka版本了

    //2.Direct模式
    //KafkaUtils.createDirectStream--API创建
    //Direct模式是直接连接到Kafka的各个分区，并拉取数据，提高了数据读取的并发能力
    //Direct模式使用的是Kafka低阶API（底层API），可以自己维护偏移量到任何地方
    //（默认是由Spark提交到默认主题/Checkpoint）
    //Direct模式+手动操作可以保证数据的Exactly-Once精准一次（数据仅会被处理一次）

    //补充知识点：SparkStreaming整合kafka的两个版本的API
    //Spark-streaming-kafka-0-8
    //支持Receiver模式和Direct模式，但是不支持offset维护API，不支持动态分区订阅..

    //Spark-streaming-kafka-0-10
    //支持Direct，不支持Receiver模式，支持offset维护API，支持动态分区订阅..
    //结论：使用Spark-streaming-kafka-0-10版本即可

    //2.准备kafka的连接参数
    val kafkaParams:Map[String,Object] = Map[String,Object](
      "bootstrap.servers"->"192.168.1.101:9092",//kafka地址
      "group.id"->"SparkKafka",
      //latest表示如果记录了偏移量则从记录的位置开始消费，如果没有记录则从最新/最后的位置开始消费
      //earliest表示如果记录了偏移量则从记录的位置开始消费，如果没有记录则从最开始/最早的位置开始消费
      //none表示如果记录了偏移量则从记录的位置开始消费，如果没有记录则报错
      "auto.offset.reset"->"latest",//偏移量重置位置
      "enable.auto.commit"->(false:java.lang.Boolean),//是否自动提交偏移量
      "key.deserializer"->classOf[StringDeserializer],
      "value.deserializer"->classOf[StringDeserializer]
    )
    val topics: Array[String] = Array("covid19_material")

    //从MySQL中查询出offsets:Map[TopicPartition, Long]
    val offsetsMap: mutable.Map[TopicPartition,Long] = OffsetUtils.getOffsetsMap("SparkKafka","covid19_material")
    val kafkaDS: InputDStream[ConsumerRecord[String,String]] = if (offsetsMap.size > 0){
      println("MySQL记录了offset信息，从offset处开始消费")
      //3.连接kafka获取消息
      KafkaUtils.createDirectStream[String,String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](topics,kafkaParams,offsetsMap))
    }else{
      println("MySQL没有记录offset信息，从latest处开始消费")
      //3.连接kafka获取消息
      KafkaUtils.createDirectStream[String,String](
        ssc,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String,String](topics,kafkaParams))
    }

    //4.实时处理分析数据
    /*val valueDS: DStream[String] = kafkaDS.map(_.value())//_表示从kafka中消费出来的每一条消息
    valueDS.print()*/
    //需求分析
    //{"from":"消耗","name":"84消毒液/瓶","num":183}
    //{"from":"采购","name":"一次性手套/副","num":476}
    //{"from":"下拨","name":"N95口罩/个","num":918}
    //{"from":"捐赠","name":"电子体温计/个","num":271}
    //{"from":"消耗","name":"医用防护服/套","num":608}
    //{"from":"需求","name":"电子体温计/个","num":193}
    //{"from":"消耗","name":"N95口罩/个","num":389}
    //{"from":"捐赠","name":"84消毒液/瓶","num":922}
    //{"from":"捐赠","name":"护目镜/副","num":929}
    //{"from":"需求","name":"医用外科口罩/个","num":590}
    //我们从kafka中消费的数据为如上格式的jsonStr，需要解析为json对象（或者是样例类）
    //目标是：将数据转为如下格式：
    //名称,采购,下拨,捐赠,消耗,需求,库存
    //N95口罩/个,1000,1000,500,-1000,-1000,500
    //护目镜/副,500,300,100,-400,-100,400
    //为了达成目标结果格式，我们需要对每一条数据进行处理，得出如下格式：
    //(name,(采购,下拨,捐赠,消耗,需求,库存))
    //如：接收到一条数据为：
    //{"from":"下拨","name":"N95口罩/个","num":918}
    //应该记为
    //(N95口罩/个,(0,918,0,0,0,918))
    //再来一条数据：
    //{"from":"消耗","name":"N95口罩/个","num":389}
    //应该记为
    //(N95口罩/个,(0,0,0,-389,0,-389))
    //最后聚合结果：
    //(N95口罩/个,(0,918,0,-389,0,529))
    //4.1将接收到的数据转换为需要的元组格式：(name,(采购,下拨,捐赠,消耗,需求,库存))
    val tupleDS: DStream[(String,(Int,Int,Int,Int,Int,Int))] = kafkaDS.map(record=>{
      val jsonStr: String = record.value()
      val jsonObj: JSONObject = JSON.parseObject(jsonStr)
      val name: String = jsonObj.getString("name")
      val from: String = jsonObj.getString("from")
      val num: Int = jsonObj.getInteger("num")
      //根据物资来源不同，将num记在不同的位置，最终形成统一的格式
      from match {
        case "采购" => (name,(num,0,0,0,0,num))
        case "下拨" => (name,(0,num,0,0,0,num))
        case "捐赠" => (name,(0,0,num,0,0,num))
        case "消耗" => (name,(0,0,0,-num,0,-num))
        case "需求" => (name,(0,0,0,0,-num,-num))
      }
    })
    tupleDS.print()
    //(84消毒液/瓶,(0,697,0,0,0,697))
    //(护目镜/副,(0,0,0,-556,0,-556))
    //(84消毒液/瓶,(0,0,0,0,-895,-895))
    //(84消毒液/瓶,(0,485,0,0,0,485))
    //(护目镜/副,(0,403,0,0,0,403))
    //(护目镜/副,(0,0,0,0,-904,-904))
    //(医用外科口罩/个,(0,0,710,0,0,710))
    //(医用外科口罩/个,(991,0,0,0,0,991))
    //(N95口罩/个,(0,0,0,-442,0,-442))
    //(N95口罩/个,(127,0,0,0,0,127))

    //4.2将上述格式的数据按照key进行聚合（有状态的计算）--使用updateStateByKey
    //定义一个函数，用来将当前批次的数据和历史数据进行聚合
    val updateFunc = (currentValues:Seq[(Int,Int,Int,Int,Int,Int)],historyValue:Option[(Int,Int,Int,Int,Int,Int)])=>{
      //0.定义变量用来接收当前批次数据
      var current_purchase: Int = 0
      var current_allocation: Int = 0
      var current_donation: Int = 0
      var current_consumption: Int = 0
      var current_demand: Int = 0
      var current_stock: Int = 0
      if(currentValues.size>0){
        //1.取出当前批次数据
        for (currentValue<-currentValues){
          current_purchase += currentValue._1
          current_allocation += currentValue._2
          current_donation += currentValue._3
          current_consumption += currentValue._4
          current_demand += currentValue._5
          current_stock += currentValue._6
        }

        //2.取出历史数据
        val history_purchase: Int = historyValue.getOrElse(0,0,0,0,0,0)._1
        val history_allocation: Int = historyValue.getOrElse(0,0,0,0,0,0)._2
        val history_donation: Int = historyValue.getOrElse(0,0,0,0,0,0)._3
        val history_consumption: Int = historyValue.getOrElse(0,0,0,0,0,0)._4
        val history_demand: Int = historyValue.getOrElse(0,0,0,0,0,0)._5
        val history_stock: Int = historyValue.getOrElse(0,0,0,0,0,0)._6

        //3.将当前批次数据和历史数据进行聚合
        val result_purchase: Int = current_purchase + history_purchase
        val result_allocation: Int = current_allocation + history_allocation
        val result_donation: Int = current_donation + history_donation
        val result_consumption: Int = current_consumption + history_consumption
        val result_demand: Int = current_demand + history_demand
        val result_stock: Int = current_stock + history_stock

        //4.将聚合结果进行返回
        Some((result_purchase,result_allocation,result_donation,result_consumption,result_demand,result_stock))
      }else{
        historyValue
      }
    }
    val resultDS: DStream[(String,(Int,Int,Int,Int,Int,Int))] = tupleDS.updateStateByKey(updateFunc)
    //resultDS.print()

    //5.将处理分析的结果存入到MySQL
    /**
     * CREATE TABLE `covid19_material` (
     * `name` varchar(12) NOT NULL DEFAULT '',
     * `purchase` int DEFAULT '0',
     * `allocation` int DEFAULT '0',
     * `donation` int DEFAULT '0',
     * `consumption` int DEFAULT '0',
     * `demand` int DEFAULT '0',
     * `stock` int DEFAULT '0',
     * PRIMARY KEY (`name`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    resultDS.foreachRDD(rdd=>{
      rdd.foreachPartition(lines=>{
        //1.开启连接
        val conn: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bigdata?serverTimezone=GMT%2B8&characterEncoding=UTF-8","root","xzx123")
        //2.编写sql并获取ps
        val sql: String = "replace into covid19_material (name,purchase,allocation,donation,consumption,demand,stock) values(?,?,?,?,?,?,?)"
        val ps: PreparedStatement = conn.prepareStatement(sql)
        //3.设置参数并执行
        for(line<-lines){
          ps.setString(1,line._1)
          ps.setInt(2,line._2._1)
          ps.setInt(3,line._2._2)
          ps.setInt(4,line._2._3)
          ps.setInt(5,line._2._4)
          ps.setInt(6,line._2._5)
          ps.setInt(7,line._2._6)
          ps.executeUpdate()
        }
        //4.关闭资源
        ps.close()
        conn.close()
      })
    })

    //6.手动提交偏移量
    //我们要手动提交偏移量，那么就意味着，消费了一批数据就应该提交一次偏移量
    //在SparkStreaming中数据抽象为DStream，DStream的底层其实也就是RDD，也就是每一批次的数据
    //所以接下来我们应该对DStream中的RDD进行处理
    kafkaDS.foreachRDD(rdd=>{
      if(rdd.count()>0){//如果该rdd中有数据则处理
        rdd.foreach(record=>println("从kafka中消费到的每一条消息："+record))
        //从kafka中消费到的每一条消息：ConsumerRecord(topic = covid19_material, partition = 2, offset = 118, CreateTime = 1644132984268, checksum = 3862035704, serialized key size = -1, serialized value size = 3, key = null, value = 789)
        //获取偏移量
        //使用Spark-streaming-kafka-0-10中封装好的API来存放偏移量并提交
        val offsets: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        /*for (o <-offsets){
          println(s"topic=${o.topic},partition=${o.partition},fromOffset=${o.fromOffset},until=${o.untilOffset}")
          //topic=covid19_material,partition=1,fromOffset=128,until=129
          //topic=covid19_material,partition=2,fromOffset=117,until=119
          //topic=covid19_material,partition=0,fromOffset=111,until=111
        }*/
        //手动提交偏移量到kafka的默认主题：__consumer__offsets中，如果开启了Checkpoint还会提交到Checkpoint中
        //kafkaDS.asInstanceOf[CanCommitOffsets].commitAsync(offsets)
        OffsetUtils.saveOffsets("SparkKafka",offsets)
      }
    })

    //7.开启SparkStreaming任务并等待结束
    ssc.start()
    ssc.awaitTermination()
  }
}
