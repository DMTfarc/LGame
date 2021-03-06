package loon.action.sprite;

import loon.LTexture;
import loon.LTextures;
import loon.canvas.Image;

public class Picture extends Entity {

	public Picture(String fileName) {
		this(fileName, 0, 0);
	}

	public Picture(int x, int y) {
		this((LTexture) null, x, y);
	}

	public Picture(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public Picture(Image image) {
		this(image.texture(), 0, 0);
	}

	public Picture(LTexture image) {
		this(image, 0, 0);
	}

	public Picture(LTexture image, int x, int y) {
		super(image);
		this.setLocation(x, y);
	}

	public boolean equals(Picture p) {
		if (null == p) {
			return false;
		}
		if (this == p) {
			return true;
		}
		if (_image.equals(p._image)) {
			return true;
		}
		if (this._width == p._width && this._height == p._height) {
			if (_image.hashCode() == p._image.hashCode()) {
				return true;
			}
		}
		return false;
	}

	public void setImage(String fileName) {
		this.setTexture(fileName);
	}

	public void setImage(LTexture image) {
		this.setTexture(image);
	}

}
