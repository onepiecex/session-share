# session-share
session 共享 通常的做法 放入redis/memcached/jdbc/hazelcast等

我的做法是 把session的所有数据通过AES对称加密
放入cookie中
我使用cookie来存放session的所有数据,

思想来源于[ninjaframework](https://github.com/ninjaframework/ninja) 的 session实现


## 基于servlet-3.1
## 参考 spring-session的设计思路
-  加入一个filter
- 利用HttpServletRequestWrapper,
实现自己的 getSession()方法 接管创建和管理Session数据的工作
- 利用HttpServletResponseWrapper 进行session数据的 save操作

- client request -> filter -> decrypt cookie to session

- response close(send or flush) -> encrypt session data to cookie

# 使用
## spring-boot
```
<dependency>
    <groupId>com.github.onepiecex.session.share.spring.boot.starter</groupId>
    <artifactId>onepiecex-session-share-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```
配置(application.yaml)
```yaml
session :
  # session cookie 的名称前缀
  prefix : prefix_cookie
  #设置为true 之后 js脚本将无法读取到cookie信息
  http_only : false
  #设置为true 之后 cookie 只能在 HTTPS 连接中被浏览器传递到服务器端进行会话验证
  transferred_over_https_only : false
  # 过期时间
  expire_time_in_seconds : 86400
  # domain
  domain : localhost
  # 加密密钥
  secret : eti8KrqgL2VYtizjeti8KrqgL2VYtizj
```

## 其他框架
```
<dependency>
  <groupId>com.github.onepiecex.session.share.core</groupId>
  <artifactId>onepiecex-session-share-core</artifactId>
  <version>1.0</version>
</dependency>
```
自行加入Filter
```ruby
RequestWrapper requestWrapper = new RequestWrapper(request,springSessionConfig);
ResponseWrapper responseWrapper = new ResponseWrapper(response,requestWrapper);
chain.doFilter(requestWrapper,responseWrapper);
```
