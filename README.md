###DragSelectRecyclerView [![Release](https://jitpack.io/v/MFlisar/DragSelectRecyclerView.svg)](https://jitpack.io/#MFlisar/DragSelectRecyclerView) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-DragSelectRecyclerView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5152)

### What is it / What does it do?
It's a simple one class `TouchListener` that can be attached to any RecyclerView and handles multi selection in google photos style via long pressing on an item and moving the finger up/down to select more items (it even scrolls if you reach the edges of the `RecyclerView`)

![Demo](https://github.com/MFlisar/DragSelectRecyclerView/blob/master/files/demo.gif?raw=true)
 
### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:

```groovy
repositories {
	maven { url "https://jitpack.io" }
}
```

2. add the compile statement to your module's `build.gradle`:

```groovy
dependencies {
	compile 'com.github.MFlisar:DragSelectRecyclerView:0.3'
}
```

### Usage - General

1. Create the a touch listener like following

```groovy
mDragSelectTouchListener = new DragSelectTouchListener()
	// check region OnDragSelectListener for more infos
	.withSelectListener(onDragSelectionListener)
	// following is all optional
	.withMaxScrollDistance(distance)    // default: 16; 	defines the speed of the auto scrolling
	.withTopOffset(toolbarHeight)       // default: 0; 		set an offset for the touch region on top of the RecyclerView
	.withBottomOffset(toolbarHeight)    // default: 0; 		set an offset for the touch region on bottom of the RecyclerView
	.withScrollAboveTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved above the top region
	.withScrollBelowTopRegion(enabled)  // default: true; 	enable auto scrolling, even if the finger is moved below the top region
	.withDebug(enabled);                // default: false;
```

2. attach it to the `RecyclerView`

```groovy
recyclerView.addOnItemTouchListener(mDragSelectTouchListener);
```

3. on item long press, inform the listener so that it can start doing it's magic

```groovy
// if one item is long pressed, we start the drag selection like following:
// we just call this function and pass in the position of the first selected item
mDragSelectTouchListener.startDragSelection(position);
```

###Usage - OnDragSelectListener

You have 3 options:

* use a simple `DragSelectTouchListener.OnDragSelectListener` => you get notified over which items the user dragged or dragged back
* use a `DragSelectTouchListener.OnAdvancedDragSelectListener` => this is an extended version of the `DragSelectTouchListener.OnDragSelectListener` which will notify you about the start and end of the drag selection as well
* **Preferred option:** use the `DragSelectionProcessor`, it implements the above mentioned interface and can be set up with 4 modes:
  * `Simple`: simply selects each item you go by and unselects on move back
  * `ToggleAndUndo`: toggles each items original state, reverts to the original state on move back
  * `FirstItemDependent`: toggles the first item and applies the same state to each item you go by and applies inverted state on move back
  * `FirstItemDependentToggleAndUndo`: toggles the item and applies the same state to each item you go by and reverts to the original state on move back
  The `DragSelectionProcessor` will take care to transform each event to the correct select/deselect event that must be handled by you afterwards. Therefore you must provide a `ISelectionHandler` in it's constructor. Just implement it's 3 simple functions and you're done. A demo can be found here: [MainActivity.java](https://github.com/MFlisar/DragSelectRecyclerView/blob/master/demo/src/main/java/com/michaelflisar/dragselectrecyclerview/demo/MainActivity.java)

```groovy
new DragSelectTouchListener.OnDragSelectListener() {
	@Override
	public void onSelectChange(int start, int end, boolean isSelected) {
		// update your selection
		// range is inclusive start/end positions
	}
}
```

###TODO

* support horizontal RecyclerViews... should be quite simple, but is not yet implemented
	
###Credits

This library is heavily inspired and based on https://github.com/weidongjian/AndroidDragSelect-SimulateGooglePhoto
