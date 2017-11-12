# ProcessCommunication
Android进程间通讯的常用方法，实现的demo,对应在不同Module中：
除了内容提供器，其他项目都是在其他进程开启服务，然后将服务作服务端，在Activity中写客户端的代码。<br>
以此实现单应用的进程间通讯，而内容提供器实现的是两个应用间的数据访问。
### 1.AIDL
对应Module:AIDL
### 2.内容提供器
对应Module:ContentProvider_provider和ContentProvider_reader，其中provider是提供内容提供器，<br>
供其他应用访问它的数据表，reader是对provider数据表进行增删查改的应用
### 3.Messenger
对应Module:Messenger
### 4.Socket通讯
对应Module:Socket
