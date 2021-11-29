package com.example.filament_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Choreographer
import android.view.Gravity
import android.view.SurfaceView
import android.widget.LinearLayout
import com.codemonkeylabs.fpslibrary.TinyDancer
import com.google.android.filament.Filament
import com.google.android.filament.Skybox
import com.google.android.filament.utils.*
import com.google.android.filament.utils.KTXLoader.createIndirectLight
import com.google.android.filament.utils.KTXLoader.createSkybox
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    private lateinit var surfaceView1: SurfaceView
    private lateinit var surfaceView2: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer1: ModelViewer
    private lateinit var modelViewer2: ModelViewer

//    private lateinit var KtxLoader: KTXLoader


    companion object {

//        init {Filament.init()}
        init { Utils.init() }

    }
/*
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(currentTime: Long) {
            choreographer.postFrameCallback(this)
            modelViewer.render(currentTime)
        }
    }
*/
// Replacing Frame Call Back
/*
    private fun Int.getTransform(): Mat4 {
        val tm = modelViewer.engine.transformManager
        return Mat4.of(*tm.getTransform(tm.getInstance(this), null))
    }

    private fun Int.setTransform(mat: Mat4) {
        val tm = modelViewer.engine.transformManager
        tm.setTransform(tm.getInstance(this), mat.toFloatArray())
    }
*/
    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun   doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer1.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            /*
            val apply = modelViewer.asset?.apply {
                modelViewer.transformToUnitCube()
                val rootTransform = this.root.getTransform()
                val degrees = 20f * seconds.toFloat()
                val zAxis = Float3(0f, 0f, 1f)
                this.root.setTransform(rootTransform * rotation(zAxis, degrees))

            }*/
//            choreographer.postFrameCallback(this)
            modelViewer1.render(currentTime)

            modelViewer2.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            /*
            val apply = modelViewer.asset?.apply {
                modelViewer.transformToUnitCube()
                val rootTransform = this.root.getTransform()
                val degrees = 20f * seconds.toFloat()
                val zAxis = Float3(0f, 0f, 1f)
                this.root.setTransform(rootTransform * rotation(zAxis, degrees))

            }*/
//            choreographer.postFrameCallback(this)
            modelViewer2.render(currentTime)
        }
    }
    
    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameCallback)
    }
    /*
    private fun loadGlb(name: String) {
        val buffer = readAsset("models/${name}.glb")
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()
    }
*/
    private fun readAsset(assetName: String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }
    private fun loadEnvironment(ibl: String) {
        // Create the indirect light source and add it to the scene.
        var buffer = readAsset("envs/${ibl}/${ibl}_ibl.ktx")
        KTXLoader.createIndirectLight(modelViewer1.engine, buffer).apply {
            var intensity = 50_000f
            modelViewer1.scene.indirectLight = this
        }

        // Create the sky box and add it to the scene.
        buffer = readAsset("envs/${ibl}/${ibl}_skybox.ktx")
        KTXLoader.createSkybox(modelViewer1.engine, buffer).apply {
            modelViewer1.scene.skybox = this
        }

        KTXLoader.createIndirectLight(modelViewer2.engine, buffer).apply {
            var intensity = 50_000f
            modelViewer2.scene.indirectLight = this
        }

        // Create the sky box and add it to the scene.
        buffer = readAsset("envs/${ibl}/${ibl}_skybox.ktx")
        KTXLoader.createSkybox(modelViewer2.engine, buffer).apply {
            modelViewer2.scene.skybox = this
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        TinyDancer.create().show(this);

        //or customazed config
        TinyDancer.create()
            .redFlagPercentage(.1f) // set red indicator for 10%
            .startingGravity(Gravity.TOP)
            .startingXPosition(200)
            .startingYPosition(600)
            .show(this);


        super.onCreate(savedInstanceState)
        //Multiple Scenes
        val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun LinearLayout.stack(v: android.view.View) {
            addView(v, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        }

        surfaceView1 = SurfaceView(this)      /*.apply { setContentView(this) }*/
        column.stack(surfaceView1)
        surfaceView2 = SurfaceView(this)
        column.stack(surfaceView2)

        setContentView(column)

        choreographer = Choreographer.getInstance()
        modelViewer1 = ModelViewer(surfaceView1)
        surfaceView1.setOnTouchListener(modelViewer1)

        modelViewer2 = ModelViewer(surfaceView2)
        surfaceView2.setOnTouchListener(modelViewer2)

//        loadGlb("DamagedHelmet")

        loadGltf("BusterDrone")
//        loadGltf("BusterDrone")
        loadEnvironment("venetian_crossroads_2k")
//        modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)
    }

    private fun loadGltf(name: String) {
        val buffer = readAsset("models/${name}.gltf")
        modelViewer1.loadModelGltf(buffer) { uri -> readAsset("models/$uri") }
        modelViewer1.transformToUnitCube()

        modelViewer2.loadModelGltf(buffer) { uri -> readAsset("models/$uri") }
        modelViewer2.transformToUnitCube()
    }



}


