package com.gezhonglei.common.log.extractor.config;

import com.gezhonglei.common.util.StringUtil;

public class BoundaryPropRule implements IPropRule {
	private String name;
	private String type;
	private int beginSkip;
	private String beginText;
	private String endText;
	private int beginIndex;
	private int endIndex = -1;
	private int length;
	private boolean leftIndelued = false;
	private boolean rightIndlued = false;
	
	public int getBeginSkip() {
		return beginSkip;
	}
	public void setBeginSkip(int beginSkip) {
		this.beginSkip = beginSkip;
	}
	public String getBeginText() {
		return beginText;
	}
	public void setBeginText(String beginText) {
		this.beginText = beginText;
	}
	public String getEndText() {
		return endText;
	}
	public void setEndText(String endText) {
		this.endText = endText;
	}
	public int getBeginIndex() {
		return beginIndex;
	}
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public boolean isLeftIndelued() {
		return leftIndelued;
	}
	public void setLeftIndelued(boolean leftIndelued) {
		this.leftIndelued = leftIndelued;
	}
	public boolean isRightIndlued() {
		return rightIndlued;
	}
	public void setRightIndlued(boolean rightIndlued) {
		this.rightIndlued = rightIndlued;
	}
	@Override
	public ParseMode mode() {
		return ParseMode.Boudary;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public String extractFrom(String text) {
		int index = 0, indexFrom, indexTo;
		if(StringUtil.isNonEmpty(beginText)) {
			int skip = this.beginSkip;
			while(skip >= 0 && index != -1) {
				index = text.indexOf(this.beginText, index);
				skip--;
			}
			indexFrom = index + (this.leftIndelued ? 0 : this.beginText.length());
		} else {
			index = this.beginIndex;
			indexFrom = index;
		}
		if(index > -1) {
			if(StringUtil.isNonEmpty(endText)) {
				indexTo = text.indexOf(endText, index+1);
				if(indexTo == -1) {
					return null;
				} else {
					indexTo += (this.rightIndlued ? endText.length() : 0);
				}
			} else {
				indexTo = this.length > 0 ? indexFrom + this.length : this.endIndex;
			}
//			indexTo = StringUtil.isNonEmpty(endText) ? text.indexOf(endText, index+1) + (this.rightIndlued ? endText.length() : 0) : 
//				this.length > 0 ? indexFrom + this.length : this.endIndex;
			return indexTo > -1 ? text.substring(indexFrom, indexTo) : text.substring(indexFrom);
		}
		return null;
	}
	
	@Override
	public String getType() {
		return type;
	}
	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
