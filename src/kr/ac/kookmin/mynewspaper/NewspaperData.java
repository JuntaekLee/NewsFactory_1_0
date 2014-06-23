/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.util.ArrayList;

/**
 * 클래스 : DB에 저장되어야할 기사 내용 모음
 **/
public class NewspaperData {

	public static final int PHOTO_NUMBER = 2;
	private String FileName;

	private int Style = -1;
	private int Color = -1;
	private String Date = null;
	private ArrayList<String> MainTitle = null;
	private ArrayList<String> SubTitle = null;
	private ArrayList<String> Content = null;
	private String[] Photo = null;

	public NewspaperData() {
		Style = -1;
		Color = -1;
		Date = null;
		MainTitle = new ArrayList<String>();
		SubTitle = new ArrayList<String>();
		Content = new ArrayList<String>();
		Photo = new String[PHOTO_NUMBER];

	}

	public ArrayList<String> getMainTitle() {
		return MainTitle;
	}

	public void setMainTitle(ArrayList<String> mainTitle) {
		MainTitle = mainTitle;
	}

	public ArrayList<String> getSubTitle() {
		return SubTitle;
	}

	public void setSubTitle(ArrayList<String> subTitle) {
		SubTitle = subTitle;
	}

	public ArrayList<String> getContent() {
		return Content;
	}

	public void setContent(ArrayList<String> content) {
		Content = content;
	}

	public String[] getPhoto() {
		return Photo;
	}

	public void setPhoto(String[] photo) {
		Photo = photo;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public int getStyle() {
		return Style;
	}

	public void setStyle(int style) {
		Style = style;
	}

	public int getColor() {
		return Color;
	}

	public void setColor(int color) {
		Color = color;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

}
