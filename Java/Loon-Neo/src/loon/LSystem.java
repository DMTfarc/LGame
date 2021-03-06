/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon;

import loon.Log.Level;
import loon.event.KeyMake;
import loon.event.SysInput;
import loon.event.Updateable;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.Dimension;
import loon.geom.Vector3f;
import loon.opengl.GLEx;
import loon.opengl.LSTRFont;
import loon.opengl.ShaderProgram;
import loon.utils.NumberUtils;
import loon.utils.StringUtils;
import loon.utils.json.JsonImpl;

public class LSystem {

	public final static EmptyObject newEmptyObject() {
		return new EmptyObject();
	}

	public static String FONT_NAME = "Dialog";

	public static String ENCODING = "UTF-8";

	public static String APP_NAME = "Loon";

	public static boolean LOW_API = true;

	public static boolean NOT_DRAG = false;

	public static boolean NOT_MOVE = false;

	public static float EMULATOR_BUTTIN_SCALE = 1f;

	public static final int DEFAULT_MAX_CACHE_SIZE = 32;

	private static float scaleWidth = 1f;

	private static float scaleHeight = 1f;

	public static final Dimension viewSize = new Dimension(480, 320);

	public static boolean PAUSED = false;

	public static boolean AUTO_REPAINT = true;

	public static boolean USE_LOG = true;

	// 是否单独切分渲染用字体
	public static boolean USE_TRUEFONT_CLIP = true;

	public static boolean LOCK_SCREEN = false;
	// 包内默认的图片路径
	public static String FRAMEWORK_IMG_NAME = "loon_";

	// 行分隔符
	final static public String LS = System.getProperty("line.separator", "\n");

	// 文件分割符
	final static public String FS = System.getProperty("file.separator", "/");

	private static IFont _defaultLogFont = null;

	private static IFont _defaultGameFont = null;

	/**
	 * 获得系统画面log中显示的字体(如果未设置则默认为本地字体渲染,字体大小16)
	 * 
	 * @return
	 */
	public final static IFont getSystemLogFont() {
		if (_defaultLogFont == null) {
			_defaultLogFont = LSTRFont.getFont(LSystem.isDesktop() ? 16 : 20);
		}
		return _defaultLogFont;
	}

	/**
	 * 设定游戏画面log中显示的字体文字
	 * 
	 * @param font
	 */
	public static void setSystemLogFont(IFont font) {
		_defaultLogFont = font;
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log,如果未设置则默认为本地字体渲染)
	 * 
	 * @return
	 */
	public final static IFont getSystemGameFont() {
		if (_defaultGameFont == null) {
			_defaultGameFont = LFont.getDefaultFont();
		}
		return _defaultGameFont;
	}

	/**
	 * 设定游戏全局默认使用的字体文字(不含log)
	 * 
	 * @param font
	 */
	public static void setSystemGameFont(IFont font) {
		_defaultGameFont = font;
	}

	/**
	 * 设定游戏全局默认使用的字体文字
	 * 
	 * @param font
	 */
	public static void setSystemGlobalFont(IFont font) {
		LSystem.setSystemLogFont(font);
		LSystem.setSystemGameFont(font);
	}

	// 默认的字符串打印完毕flag
	public static String FLAG_TAG = "▼";

	// 版本号(正在不断完善中,试图把此版做成API以及功能基本稳定的版本,以后只优化与扩展api，而不替换删除api……)
	public final static String version = "0.5";

	public final String getVersionString() {
		return version;
	}

	public final static Vector3f getVersion() {
		return new Vector3f(0, 5, 0);
	}

	public static int MODE_NORMAL = 1;

	public static int MODE_ALPHA_MAP = 2;

	public static int MODE_ALPHA_BLEND = 3;

	public static int MODE_COLOR_MULTIPLY = 4;

	public static int MODE_ADD = 5;

	public static int MODE_SCREEN = 6;

	public static int MODE_ALPHA = 7;

	public static int MODE_SPEED = 8;

	public static int MODE_ALPHA_ONE = 9;

	public static int MODE_NONE = 10;

	public static int MODE_MASK = 11;

	public static int MODE_LIGHT = 12;

	public static int MODE_ALPHA_ADD = 13;

	public static int MODE_MULTIPLY = 14;
	// 兆秒
	final static public long MSEC = 1;

	// 秒
	final static public long SECOND = 1000;

	// 分
	final static public long MINUTE = SECOND * 60;

	// 小时
	final static public long HOUR = MINUTE * 60;

	// 天
	final static public long DAY = HOUR * 24;

	// 周
	final static public long WEEK = DAY * 7;

	// 理论上一年
	final static public long YEAR = DAY * 365;

	// 是否使用了HTML5环境
	private static boolean _USE_HTML5 = false;
	private static boolean _USE_MOBILE = false;

	static LGame _base;

	static LProcess _process;

	static Platform _platform;

	static JsonImpl _json_instance;

	public static Platform platform() {
		return _platform;
	}

	public static LGame base() {
		if (_base != null) {
			return _base;
		} else if (_platform != null) {
			_base = _platform.getGame();
		}
		return _base;
	}

	protected static void initProcess(LGame game) {
		_base = game;
		LSetting setting = _base.setting;
		setting.updateScale();
		LSystem.viewSize.setSize(setting.width, setting.height);
		_process = new LProcess(game);
		_USE_HTML5 = (_base.type() == LGame.Type.HTML5);
		_USE_MOBILE = (_base.isMobile());
		_base.log().debug("The Loon Game Engine is Begin");
	}

	public static void exit() {
		if (_platform != null) {
			_platform.close();
		}
	}

	public static void sysText(SysInput.TextEvent event, KeyMake.TextType textType, String label, String initialValue) {
		if (_platform != null) {
			_platform.sysText(event, textType, label, initialValue);
		}
	}

	public static void sysDialog(SysInput.ClickEvent event, String title, String text, String ok, String cancel) {
		if (_platform != null) {
			_platform.sysDialog(event, title, text, ok, cancel);
		}
	}

	public static Json json() {
		if (_json_instance == null) {
			_json_instance = new JsonImpl();
		}
		return _json_instance;
	}

	public static boolean isHTML5() {
		return LSystem._USE_HTML5;
	}

	public static boolean isMobile() {
		return LSystem._USE_MOBILE;
	}

	public static boolean isDesktop() {
		return !_USE_MOBILE && !_USE_HTML5;
	}

	public static float getScaleWidth() {
		return LSystem.scaleWidth;
	}

	public static float getScaleHeight() {
		return LSystem.scaleHeight;
	}

	public static void setScaleWidth(float sx) {
		LSystem.scaleWidth = sx;
	}

	public static void setScaleHeight(float sy) {
		LSystem.scaleHeight = sy;
	}

	public static float invXScaled(float length) {
		return length / LSystem.getScaleWidth();
	}

	public static float invYScaled(float length) {
		return length / LSystem.getScaleWidth();
	}

	public static String getAllFileName(String name) {
		if (name == null) {
			return "";
		}
		int idx = name.lastIndexOf('.');
		return idx == -1 ? name : name.substring(0, idx);
	}

	public static String getFileName(String name) {
		if (name == null) {
			return "";
		}
		int length = name.length();
		int idx = name.lastIndexOf('/');
		if (idx == -1) {
			idx = name.lastIndexOf('\\');
		}
		int size = idx + 1;
		if (size < length) {
			return name.substring(size, length);
		} else {
			return "";
		}
	}

	public static String getExtension(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(index + 1);
		}
	}

	public static boolean mainDrawRunning() {
		if (_base == null) {
			return false;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			return gl.running();
		}
		return false;
	}

	public static void mainBeginDraw() {
		if (_base == null) {
			return;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			if (!gl.running()) {
				gl.begin();
			}
		}
	}

	public static void mainEndDraw() {
		if (_base == null) {
			return;
		}
		Display game = _base.display();
		if (game != null) {
			GLEx gl = game.GL();
			if (gl.running()) {
				gl.end();
				gl.freeBatchBuffer();
			}
		}
	}

	public final static void close(LRelease rel) {
		if (rel != null) {
			try {
				rel.close();
				rel = null;
			} catch (Exception e) {
			}
		}
	}

	public final static LProcess getProcess() {
		return _process;
	}

	public final static void load(Updateable u) {
		if (_process != null) {
			_process.addLoad(u);
		}
	}

	public final static void unload(Updateable u) {
		if (_process != null) {
			_process.addUnLoad(u);
		}
	}

	public static final int VERTEX_SIZE = 2 + 1 + 2;
	public static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

	static public String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasNormals ? "attribute vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" : "")
				+ (hasColors ? "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}

		shader += "uniform mat4 u_projModelView;\n";
		shader += (hasColors ? "varying vec4 v_col;\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
		}

		shader += "void main() {\n" + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}
		shader += "   gl_PointSize = 1.0;\n";
		shader += "}\n";
		return shader;
	}

	static public String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n";

		if (hasColors)
			shader += "varying vec4 v_col;\n";
		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
			shader += "uniform sampler2D u_sampler" + i + ";\n";
		}

		shader += "void main() {\n" + "   gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");

		if (numTexCoords > 0)
			shader += " * ";

		for (int i = 0; i < numTexCoords; i++) {
			if (i == numTexCoords - 1) {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
			} else {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
			}
		}

		shader += ";\n}";
		return shader;
	}

	static public ShaderProgram createDefaultShader() {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false)
			throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}

	static public String createGlobalVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasNormals ? "attribute vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" : "")
				+ (hasColors ? "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}

		shader += "uniform mat4 u_projModelView;\n";
		shader += (hasColors ? "uniform vec4 v_col;\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
		}

		shader += "void main() {\n" + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}
		shader += "   gl_PointSize = 1.0;\n";
		shader += "}\n";
		return shader;
	}

	static public String createGlobalFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n";
		if (hasColors) {
			shader += "uniform vec4 v_col;\n";
		}
		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
			shader += "uniform sampler2D u_sampler" + i + ";\n";
		}
		shader += "void main() {\n" + "   gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
		if (numTexCoords > 0) {
			shader += " * ";
		}
		for (int i = 0; i < numTexCoords; i++) {
			if (i == numTexCoords - 1) {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
			} else {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
			}
		}
		shader += ";\n}";
		return shader;
	}

	static public ShaderProgram createGlobalShader() {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "uniform LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		}
		return shader;
	}

	public final static String format(float value) {
		String fmt = String.valueOf(value);
		return fmt.indexOf('.') == -1 ? (fmt + ".0") : fmt;
	}

	public final static int unite(int hashCode, boolean value) {
		int v = value ? 1231 : 1237;
		return unite(hashCode, v);
	}

	public final static int unite(int hashCode, long value) {
		int v = (int) (value ^ (value >>> 32));
		return unite(hashCode, v);
	}

	public final static int unite(int hashCode, float value) {
		int v = NumberUtils.floatToIntBits(value);
		return unite(hashCode, v);
	}

	public final static int unite(int hashCode, Object value) {
		return unite(hashCode, value.hashCode());
	}

	public final static int unite(int hashCode, int value) {
		return 31 * hashCode + value;
	}

	public final static boolean checkAngle(float angle, float actual) {
		return actual > angle - 22.5f && actual < angle + 22.5f;
	}

	public static boolean isImage(String extension) {
		return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("bmp")
				|| extension.equals("gif");
	}

	public static boolean isText(String extension) {
		return extension.equals("json") || extension.equals("xml") || extension.equals("txt")
				|| extension.equals("glsl") || extension.equals("fnt") || extension.equals("pack")
				|| extension.equals("obj") || extension.equals("atlas") || extension.equals("g3dj")
				|| extension.equals("tmx") || extension.equals("an") || extension.equals("text")
				|| extension.equals("cfg") || extension.equals("cvs");
	}

	public static boolean isAudio(String extension) {
		return extension.equals("mp3") || extension.equals("ogg") || extension.equals("wav") || extension.equals("mid");
	}

	public final static void debug(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg);
		}
	}

	public final static void debug(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg, args);
		}
	}

	public final static void debug(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().debug(msg, throwable);
		}
	}

	public final static void info(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg);
		}
	}

	public final static void info(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg, args);
		}
	}

	public final static void info(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().info(msg, throwable);
		}
	}

	public final static void warn(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().warn(msg);
		}
	}

	public final static void warn(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().warn(msg, args);
		}
	}

	public final static void warn(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().warn(msg, throwable);
		}
	}
	
	public final static void error(String msg) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg);
		}
	}

	public final static void error(String msg, Object... args) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg, args);
		}
	}

	public final static void error(String msg, Throwable throwable) {
		if (LSystem._base != null) {
			LSystem._base.log().error(msg, throwable);
		}
	}

	public final static RuntimeException runThrow(String msg) {
		error(msg);
		return new RuntimeException(msg);
	}

	public final static RuntimeException runThrow(String msg, Throwable thr) {
		error(msg, thr);
		return new RuntimeException(msg, thr);
	}

	public final static RuntimeException runThrow(String msg, Object... args) {
		error(msg, args);
		return new RuntimeException(StringUtils.format(msg, args));
	}

	public final static void d(String msg) {
		debug(msg);
	}

	public final static void d(String msg, Object... args) {
		debug(msg, args);
	}

	public final static void d(String msg, Throwable throwable) {
		debug(msg, throwable);
	}

	public final static void i(String msg) {
		info(msg);
	}

	public final static void i(String msg, Object... args) {
		info(msg, args);
	}

	public final static void i(String msg, Throwable throwable) {
		info(msg, throwable);
	}

	public final static void w(String msg) {
		warn(msg);
	}

	public final static void w(String msg, Object... args) {
		warn(msg, args);
	}

	public final static void w(String msg, Throwable throwable) {
		warn(msg, throwable);
	}

	public final static void e(String msg) {
		error(msg);
	}

	public final static void e(String msg, Object... args) {
		error(msg, args);
	}

	public final static void e(String msg, Throwable throwable) {
		error(msg, throwable);
	}

	public final static RuntimeException re(String msg) {
		return runThrow(msg);
	}

	public final static RuntimeException re(String msg, Throwable thr) {
		return runThrow(msg, thr);
	}

	public final static RuntimeException re(String msg, Object... args) {
		return runThrow(msg, args);
	}

	public final static void setLogMinLevel(Level level) {
		if (LSystem._base != null) {
			LSystem._base.log().setMinLevel(level);
		}
	}

}
