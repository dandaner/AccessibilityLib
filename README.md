# AccessibilityLib
Android Accessibility Dev Lib
基于Android系统Accessibility Service，做的一个统一的消息处理逻辑。

###Good news
- 支持跨进程访问
- 友好的业务扩展性

###已知的问题：
- 系统API所限，AccessibilityService#setServiceInfo(AccessibilityServiceInfo info) 动态监听逻辑不完善，
只能做加法，不能做减法，会导致监听越来越多。比如：可以动态增加监听的应用，但是无法动态减少监听的应用。
- 没有做适配工作，特别是在做event过滤以及执行的时候，如果投入项目，需要覆盖机型适配工作。
- 由于系统限制，无法多条业务逻辑无法共存，比如：加速，自动卸载/自动安装，红包等，无法并发。比如正在处理自动安装
过程中，突然弹出其他界面，会导致安装界面无法解析。
- AccessibilityNodeInfo#performAction（） 返回值并不可靠，比如，点击一个按钮会弹窗，但是无论该按钮performAction
的返回值是什么，都有可能无法弹窗。

###有意思的问题：
- 通过测试发现，AccessibilityService#onAccessibilityEvent(AccessibilityEvent event)分发的原始AccessibilityEvent
对象是无法在一个线程中解析的，当在线程中调用AccessibilityEvent的接口时，很多数据都无法获取，AccessibilityEvent#recycle()
会抛出IllegalStateException,但是UI线程中，没有任何问题。
- 如果将系统分发的原始AccessibilityEvent放入EventQueue队列中，则在线程中执行AccessibilityEvent的解析，执行以及释放都没有
任何问题，通过EventQueue#enqueue()，我们发现，入队列的并不是原始event对象，而只是原始对象的拷贝AccessibilityEvent.obtain(event)

-----

本项目参考了AOSP中的talkback项目