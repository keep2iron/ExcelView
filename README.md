![](http://i.imgur.com/Tz6w0UR.gif)

支持垂直和水平的固定item,可以进行手势缩放（不过还是有点问题）支持跨行跨列，在数据量较大的情况下可能支持不是很好。

添加左上角固定位置数据

````
reportLayout.setLeftTopController(new CellController(getApplicationContext(), setLeftTopData(), 4, false));
//设置左上角数据，4是代表4列，false代表无法进行移动和手势缩放

reportLayout.setFixedHorizontalController(new CellController(getApplicationContext(), setHeader(), 36, false));
//设置水平方向固定数据

reportLayout.setFixedVerticalController(new CellController(getApplicationContext(), setLeftData(10, 4), 4, false));
//设置垂直方向固定数据

reportLayout.setContentViewController(new CellController(getApplicationContext(), setData(10, 36), 36, true));
//设置设置内容显示区域
````
