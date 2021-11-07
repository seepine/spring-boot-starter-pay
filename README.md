# spring-boot-starter-pay

集成支付接口

- payJs

## spring boot starter依赖

```xml
<dependency>
  <groupId>com.seepine</groupId>
  <artifactId>spring-boot-starter-pay</artifactId>
  <version>0.1.2</version>
</dependency>
```

## 使用配置

### 1.配置文件
application.yml
```
pay-js:
  m-ch-id: ${your mChId}
  secret: ${your secret}
  notify-url: ${your notifyUrl}
```

### 2.代码使用
注入template
```java
@Autowire
private PayJsTemplate payJsTemplate;
```

## 方法介绍
### 1.native支付（返回支付二维码）
默认回调地址
```java
PayJsRes payRes = payJsTemplate.channel().pay(String subject, //商品标题
        String outTradeNo, //已方生成订单号
        Double amount); //金额，单位元
```
手动填写回调地址
```java
PayJsRes payRes = payJsTemplate.channel().pay(String subject, //商品标题
        String outTradeNo, //已方生成订单号
        Double amount, //金额，单位元
        String notifyUrl); //异步通知回调地址
```
### 2.异步通知回调验签
```java
    @ResponseBody
    @PostMapping(value = "notify")
    public Object fallback(PayJsReq payJsReq){
        Boolean isSign = payJsTemplate.channel().checkSign(payJsReq);
        //业务逻辑...
    }
```
### 3.查询订单
一般用于关闭订单前主动查询订单状态，避免已支付成功但回调未收到，误操作业务订单状态
```java
// status1表示支付成功，其他参数可查看PayJsRes中带有check注解，或查询官方文档
PayJsRes payRes = payJsTemplate.channel().check(String payJsOrderId);
```
### 4.关闭订单
用来主动关闭订单，避免业务订单已设为过期，但买家又能继续扫码支付
```java
// status1表示支付成功，其他参数可查看PayJsRes中带有check注解，或查询官方文档
PayJsRes payRes = payJsTemplate.channel().close(String payJsOrderId);
```

## 多商户配置

### 1.配置文件
application.yml,以逗号隔开
```
pay-js:
  m-ch-id: ${mChId1},${mChId2}
  secret: ${secret1},${secret2}
  notify-url: ${notifyUrl1},${notifyUrl1}
```

### 2.注入代码使用
注入template
```java
@Autowire
private PayJsTemplate payJsTemplate;
```
### 3.native支付（返回支付二维码）
通过channel(index)来指定商户，从0开始
```java
PayJsRes payRes = payJsTemplate.channel(${index}).pay(String subject, //商品标题
        String outTradeNo, //已方生成订单号
        Double amount); //金额，单位元
```