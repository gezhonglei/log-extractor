package com.gezhonglei.common.log.extractor.config;

import com.gezhonglei.common.util.StringUtil;

public class BoundaryPropRule extends PropRule {
	private int beginSkip;
	private String beginText;
	private String endText;
	private int beginIndex;
	private int endIndex = -1;
	private int length;
	private boolean leftIncluded = false;
	private boolean rightIncluded = false;
	
	public BoundaryPropRule() {
		this.mode = "boundary";
	}
	
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
	public boolean isLeftIncluded() {
		return leftIncluded;
	}
	public void setLeftIncluded(boolean leftIncluded) {
		this.leftIncluded = leftIncluded;
	}
	public boolean isRightIncluded() {
		return rightIncluded;
	}
	public void setRightIncluded(boolean rightIncluded) {
		this.rightIncluded = rightIncluded;
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
			indexFrom = index + (this.leftIncluded ? 0 : this.beginText.length());
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
					indexTo += (this.rightIncluded ? endText.length() : 0);
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
}
