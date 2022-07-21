package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context,attrs:AttributeSet): View(context,attrs) {
    private var mDrawPath: CustomPath? = null // An variable of CustomPath inner class to use it further.
    private var mCanvasBitmap: Bitmap? = null // An instance of the Bitmap.
    private var mDrawPaint: Paint? = null // The Paint class holds the style and color information about how to draw geometries, text and bitmaps.
    private var mCanvasPaint: Paint? = null // Instance of canvas paint view.
    private var mBrushSize: Float = 0.toFloat() // A variable for stroke/brush size to draw on the canvas.
    private var color = Color.BLACK // A variable to hold a color of the stroke.
    private var canvas: Canvas? = null
    private val mPaths =  ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mBrushSize = 20.toFloat()
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE // This is to draw a STROKE style
        mDrawPaint?.strokeJoin = Paint.Join.ROUND // This is for store join
        mDrawPaint?.strokeCap = Paint.Cap.ROUND // This is for stroke Cap
    }

    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
        super.onSizeChanged(w, h, wprev, hprev)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (path in mPaths){
            mDrawPaint?.strokeWidth = path.brushThickness
            mDrawPaint?.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }


        if (!mDrawPath!!.isEmpty) {
            mDrawPaint?.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint?.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // Touch event of X coordinate
        val touchY = event.y // touch event of Y coordinate

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath?.color = color
                mDrawPath?.brushThickness = mBrushSize

                mDrawPath?.reset() // Clear any lines and curves from the path, making it empty.
                mDrawPath?.moveTo(
                    touchX,
                    touchY
                ) // Set the beginning of the next contour to the point (x,y).
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawPath?.lineTo(
                    touchX,
                    touchY
                ) // Add a line from the last point to the specified point (x,y).
            }

            MotionEvent.ACTION_UP -> {

                mDrawPath = CustomPath(color, mBrushSize)

                mPaths.add(mDrawPath!!)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize, resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun undoPath(){

        if(mPaths.size>1) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 2))
            invalidate()
        }
    }


    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint?.color = color
    }
    internal inner class CustomPath(var color:Int,var brushThickness:Float):Path()
}