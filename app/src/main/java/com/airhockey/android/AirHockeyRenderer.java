/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.util.LoggerConfig;
import com.airhockey.android.util.ShaderHelper;
import com.airhockey.android.util.TextResourceReader;

public class AirHockeyRenderer implements Renderer {
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";    
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;
    private final Context context;
    private int program;
    private int uColorLocation;
    private int aPositionLocation;

    public AirHockeyRenderer() {
        // This constructor shouldn't be called -- only kept for showing
        // evolution of the code in the chapter.
        context = null;
        vertexData = null;
    }

    public AirHockeyRenderer(Context context) {
        this.context = context;
        
        /*
		float[] tableVertices = { 
			0f,  0f, 
			0f, 14f, 
			9f, 14f, 
			9f,  0f 
		};
         */
        /*
		float[] tableVerticesWithTriangles = {
			// Triangle 1
			0f,  0f, 
			9f, 14f,
			0f, 14f,

			// Triangle 2
			0f,  0f, 
			9f,  0f,							
			9f, 14f			
			// Next block for formatting purposes
			9f, 14f,
			, // Comma here for formatting purposes			

			// Line 1
			0f,  7f, 
			9f,  7f,

			// Mallets
			4.5f,  2f, 
			4.5f, 12f
		};
         */
        float[] tableVerticesWithTriangles = {
            // Triangle 1
            /*-0.5f, -0.5f,
             0.5f,  0.5f,
            -0.5f,  0.5f,

            // Triangle 2
            -0.5f, -0.5f, 
             0.5f, -0.5f, 
             0.5f,  0.5f,

            // Line 1
            -0.5f, 0f, 
             0.5f, 0f,

            // Mallets
            0f, -0.25f,
            0f,  0.25f*/

                 //line 1
            -.42f,0f,
            .32f,0f,
            //Triangle 1
                -.42f,0f,
                -.3f,0f,
                -.26f,.1f,
             //Triangle 2
                0.0f,.15f,
                //Trinagle 3
                -.3f,0f,
                -0.02f,0f,
                //Trinalge 4
                0.0f,.15f,
                0f,0f,
                //Triangle 5
                0.02f,0f,
                //Triangle 6
                0.0f,.15f,
                0.02f,0f,
                .26f,.1f,
                //Triangle 7
                0.02f,0f,
                .32f,0f,
                .26f,.1f,
                //Triangle 8
                -.27f,-.15f,
                0f,0f,
                0f,-.15f,
                //Triangle 9
                -.27f,0f,
                -.27f,-.15f,
                0f,0f,
                //Triangle 10
                0f,-.15f,
                .32f,0f,
                0f,0f,
                //Triangle 11
                .32f,0f,
                0f,-.15f,
                .35f,-.15f,
                //Triangle 12
                -.42f,0f,
                -.26f,.1f,
                -.9f,0f,
                //Triangle 13
                -.42f,0f,
                -.45f,-.08f,
                -.9f,0f,
                //Triangle 14
                -.9f,0f,
                -.68f,-.15f,
                -.45f,-0.08f,
                //Triangle 15
                -.9f,0f,
                -.9f,-.15f,
                -.68f,-.15f,
                //Triangle 16
                -.45f,-.08f,
                -.27f,-.15f,
                -.27f,0f,
                //Triangle 17
                -.42f,0f,
                -.45f,-.08f,
                -.27f,0f,
                //triangle 18
                .32f,0f,
                .5f,.08f,
                .26f,.1f,
                //tri 19
                .5f,.08f,
                .68f,-0.08f,
                .9f,-0.02f,
                //tri 20
                .5f,.08f,
                .32f,0f,
                .32f,-0.01f,
                //tri 21
                .35f,-.15f,
                .42f,-.15f,
                .68f,-0.08f,
                //tri 22
                .35f,-.15f,
                .32f,-0.01f,
                .68f,-.08f,
                //tri 23
                .68f,-.08f,
                .5f,.08f,
                .3f,-.02f,
                //tri 24
                .68f,-.08f,
                .9f,-0.02f,
                .85f,-.15f,
                //tri 25
                .9f,-0.02f,
                .85f,-.15f,
                .98f,-.1f,
                //tire line 1
                -.45f,-.17f,
                -.35f,-.17f,
                -.45f,-.11f,
                //tire line 2
                -.45f,-.17f,
                -.45f,-.25f,
                -.55f,-.17f,
                //tire line 3
                -.45f,-.17f,
                -.45f,-.25f,
                -.35f,-.17f,
                //tire line 4
                -.45f,-.17f,
                -.45f,-.11f,
                -.55f,-.17f,
                //tire2 tri 1
                .68f,-0.11f,
                .58f,-0.17f,
                .78f,-0.17f,
                //tire tri 2
                .58f,-0.17f,
                .78f,-0.17f,
                .68f,-0.25f





























        };
        
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        /*
		// Set the background clear color to red. The first component is red,
		// the second is green, the third is blue, and the last component is
		// alpha, which we don't use in this lesson.
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
         */

        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        String vertexShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, 
            false, 0, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     * 
     * @param width
     *            The new width, in pixels.
     * @param height
     *            The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);		
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        
        // Draw the table.
        //glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        //glDrawArrays(GL_TRIANGLES, 0, 6);
        
        // Draw the center dividing line.
        //glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
       // glDrawArrays(GL_LINES, 6, 2);
        
        // Draw the first mallet blue.        
        //glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        //glDrawArrays(GL_POINTS, 8, 1);

        // Draw the second mallet red.
       // glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        //glDrawArrays(GL_POINTS, 9, 1);

        //line1
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 0, 2);

        //triangle1
        glUniform4f(uColorLocation, 0.0f, 0.5f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 2, 3);

        //triangle 2
        glUniform4f(uColorLocation, 0.9f, 0.9f, 0.9f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 3, 3);
        //triangle 3
        glUniform4f(uColorLocation, 0.9f, 0.9f, 0.9f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 5, 3);
        //triangle4
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 7, 3);
        //triangle 5
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 8, 3);
        //triangle 6
        glUniform4f(uColorLocation, 0.9f, 0.9f, 0.9f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 11, 3);
        //triangle 7
        glUniform4f(uColorLocation, 0.9f, 0.9f, 0.9f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 14, 3);
        //triangle 8
        glUniform4f(uColorLocation, 0.6f, 0.0f, 0.6f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 17, 3);
        //triangle 9
        glUniform4f(uColorLocation, 0.6f, 0.0f, 0.6f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 20, 3);
        //triangle 10
        glUniform4f(uColorLocation, .6f, 0.0f, 0.6f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 23, 3);
        //triangle 11
        glUniform4f(uColorLocation, 0.6f, 0.0f, 0.6f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 26, 3);
        //triangle 12
        glUniform4f(uColorLocation, 0.9f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 29, 3);
        //triangle 13
        glUniform4f(uColorLocation, 0.2f, 0.2f, 0.2f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 32, 3);
        //trinagle 14
        glUniform4f(uColorLocation, 0.2f, 0.2f, 0.2f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 35, 3);
        //trinagle 15
        glUniform4f(uColorLocation, 0.2f, 0.2f, 0.2f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 38, 3);
        //trianlge 16
        glUniform4f(uColorLocation, 0.0f, .8f, 0.8f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 41, 3);
        //traingle 17
        glUniform4f(uColorLocation, 0.0f, .8f, .8f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 44, 3);
        //tri 18
        glUniform4f(uColorLocation, 1.0f, 0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 47, 3);
        //tri 19
        glUniform4f(uColorLocation, 0.0f, 0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 50, 3);
        //tri 20
        glUniform4f(uColorLocation, 0.4f, 1.0f, 0.4f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 53, 3);
        //tri 21
        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 56, 3);
        //tri 22
        glUniform4f(uColorLocation, 0f, 0.8f, 0.8f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 59, 3);
        //tri 23
        glUniform4f(uColorLocation, 0f, 0.8f, 0.8f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 62, 3);
        //tri 24
        glUniform4f(uColorLocation, 0.0f, 0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 65, 3);
        //tri 25
        glUniform4f(uColorLocation, 0.0f, 0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 68, 3);
        //line 26
        glUniform4f(uColorLocation, 0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 71, 3);
        //tire line 2
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 74, 3);
        //tire line 3
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 77, 3);
        //tire line 4
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 80, 3);
        //tire 2 line 1
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 83, 3);
        //tire 2 line 2
        glUniform4f(uColorLocation, 0.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 86, 3);










    }
}
