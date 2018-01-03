**Fork一下，方便在源码上写注释**

jar包下载速度很慢的时候，就用阿里云的Maven镜像仓库试试。

[如何开始调试可以参考这里。](https://www.jianshu.com/p/ad9764022e8a)

## 踩坑一
Producer第一次产生消息成功，第二次就会报错:`service not available now, maybe disk full, CL:  0.98 CQ:  0.98 INDEX:  0.98, maybe your broker machine memory too small.`

原因：磁盘空间太小，日志不够了。要嘛就修改日志的打印位置，要嘛就删除磁盘上其他的东西。（我删到有5G的空间，有点作用，但是多启动几次Producer项目，还是可以重现这个问题。）