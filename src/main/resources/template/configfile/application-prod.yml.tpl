# 全局配置

#服务日志
service:
  controller:
    writeAccessLog: false

# 开发环境配置
server:
  # 服务器的HTTP端口，默认为8080
  port: 8080
  servlet:
    # 应用的访问路径
    context-path:
  tomcat:
    # tomcat的URI编码
    uri-encoding: UTF-8
    # 连接数满后的排队数，默认为100
    accept-count: 1000
    threads:
      # tomcat最大线程数，默认为200
      max: 800
      # Tomcat启动初始化的线程数，默认值10
      min-spare: 100
  max-http-header-size: 4048576

# Spring配置
spring:
  datasource: #数据库连接的配置
    driver-class-name:
    url:
    username: root
    password: 123456

  component-scan:
    base-package: ${groupId}.*

  thymeleaf:
    mode: HTML5
    encoding: UTF-8
    servlet:
      content-type: text/html
    cache: false
    prefix: classpath:/static/
    suffix: .html

  # redis 配置
  redis:
    # 地址
    host: 172.0.0.1
    # 端口，默认为6379
    port: 6379
    # 数据库索引
    database: 1
    # 密码
    #password:
    # 连接超时时间
    timeout: 10s
    lettuce:
      pool:
        # 连接池中的最小空闲连接
        min-idle: 0
        # 连接池中的最大空闲连接
        max-idle: 8
        # 连接池的最大数据库连接数
        max-active: 8
        # #连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1ms

flowable:
  #  #关闭自动部署验证设置
  #  check-process-definitions: true
  #  #设置自动创建表为true
  #  database-schema-update: false
  #  database-schema:
  #  #关闭定时任务JOB
  async-executor-activate: false
  #  #校验流程文件，默认校验resources下的processes文件夹里的流程文件
  process-definition-location-prefix: classpath*:/processes/
  process-definition-location-suffixes: "**.bpmn20.xml, **.bpmn"

#cache缓存配置
cache:
  category:
    - name: ${groupId}.session
      seconds: 7200
      refresh: true

    - name: file
      seconds: 1200
      refresh: true

# MyBatis配置
mybatis:
  # 搜索指定包别名
  typeAliasesPackage: ${groupId}.**.domain,${groupId}.**.entity,${groupId}.**.po
  # 配置mapper的扫描，找到所有的mapper.xml映射文件
  mapperLocations: classpath*:mapper/**/mybatis-*.xml,classpath*:${groupIdPath}/**/mybatis-*.xml
  # 加载全局的配置文件
  configLocation: classpath:mybatis/mybatis-config.xml

