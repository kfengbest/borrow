package com.intalker.borrow.ui.book;

import com.intalker.borrow.R;
import com.intalker.borrow.data.BookInfo;
import com.intalker.borrow.util.LayoutUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookDetailDialog extends Dialog {

	private RelativeLayout mContent = null;
	private RelativeLayout mLayout = null;
	private ImageView mCoverImage = null;
	private RelativeLayout mDetailInfoPanel = null;
	
	private TextView mNameTextView = null;
	private TextView mAuthorTextView = null;
	private TextView mPublisherTextView = null;
	private TextView mPageCountTextView = null;
	private TextView mISBNTextView = null;
	
	public BookDetailDialog(Context context) {
		super(context, R.style.Theme_TransparentDialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setCanceledOnTouchOutside(true);

		mContent = new RelativeLayout(context);
		this.setContentView(mContent);
		
		mLayout = new RelativeLayout(context);
		mLayout.setBackgroundResource(R.drawable.detail_bk);
		RelativeLayout.LayoutParams mainLayoutLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mainLayoutLP.width = LayoutUtil.getDetailDialogWidth();
		mainLayoutLP.height = LayoutUtil.getDetailDialogHeight();
		mContent.addView(mLayout, mainLayoutLP);
		
		mCoverImage = new ImageView(context);
		RelativeLayout.LayoutParams coverImageLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		coverImageLP.width = LayoutUtil.getDetailDialogWidth() / 2;
		coverImageLP.height = LayoutUtil.getDetailDialogHeight() / 2;
		int boundMargin = LayoutUtil.getDetailDialogBoundMargin();
		coverImageLP.leftMargin = boundMargin;
		coverImageLP.topMargin = boundMargin;
		mCoverImage.setImageResource(R.drawable.bookcover_unknown);
		mCoverImage.setScaleType(ScaleType.FIT_START);
		mCoverImage.setBackgroundColor(Color.BLUE);
		mLayout.addView(mCoverImage, coverImageLP);
		
		mDetailInfoPanel = new RelativeLayout(context);
		mDetailInfoPanel.setBackgroundColor(Color.WHITE);
		RelativeLayout.LayoutParams detailInfoPanelLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		detailInfoPanelLP.width = LayoutUtil.getDetailDialogWidth() / 2 - boundMargin * 3;
		detailInfoPanelLP.height = LayoutUtil.getDetailDialogHeight() / 2;
		detailInfoPanelLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		detailInfoPanelLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		detailInfoPanelLP.topMargin = boundMargin;
		detailInfoPanelLP.rightMargin = boundMargin;
		mLayout.addView(mDetailInfoPanel, detailInfoPanelLP);
		
		mNameTextView = addInfoTextView(R.string.book_name, 0);
		mAuthorTextView = addInfoTextView(R.string.author, 1);
		mPublisherTextView = addInfoTextView(R.string.publisher, 2);
		mPageCountTextView = addInfoTextView(R.string.page_count, 3);
		mISBNTextView = addInfoTextView(R.string.isbn, 4);
	}
	
	private TextView addInfoTextView(int textResId, int index)
	{
		Context context = this.getContext();
		TextView label = new TextView(context);
		label.setText(textResId);
		
		RelativeLayout.LayoutParams labelLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		int lineHeight = LayoutUtil.getDetailInfoLineHeight();
		int topMargin = lineHeight * index * 5 / 2;
		labelLP.topMargin = topMargin;
		
		mDetailInfoPanel.addView(label, labelLP);
		
		TextView valueText = new TextView(context);
		valueText.setText("???");
		
		RelativeLayout.LayoutParams valueTextLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		valueTextLP.topMargin = topMargin + lineHeight;
		valueTextLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mDetailInfoPanel.addView(valueText, valueTextLP);
		
		return valueText;
	}
	
	public void setInfo(BookInfo bookInfo)
	{
		if(null != bookInfo)
		{
			mCoverImage.setImageBitmap(bookInfo.getCoverImage());
			mNameTextView.setText(bookInfo.getName());
			mAuthorTextView.setText(bookInfo.getAuthor());
			mPublisherTextView.setText(bookInfo.getPublisher());
			mPageCountTextView.setText(bookInfo.getPageCount());
			mISBNTextView.setText(bookInfo.getISBN());
		}
	}

}
