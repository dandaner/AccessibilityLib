# AccessibilityLib
Android Accessibility Dev Lib
基于Android系统Accessibility Service，做的一个统一的消息处理逻辑。

###Good news
- 支持跨进程访问
- 高度自由的扩展性
- 支持多条业务逻辑并存，比如抢红包，加速，自动安装，自动卸载等业务逻辑可以共存。（由于系统限制，同一时刻只能执行一条业务）

###Bad news
- 动态监听逻辑不完善，只能做加法，不能做减法，会导致监听越来越多。（系统API所限，目前没什么好办法）
- 没有做适配工作，可能存在比较多的坑，特别是在做event过滤以及执行的时候，如果投入项目，需要做大量的适配工作。