package uk.co.beamsy.bookzap.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import uk.co.beamsy.bookzap.R;
import uk.co.beamsy.bookzap.model.UserBook;

/**
 * Created by jake on 15/11/17.
 */

public class BookCardAdaptor extends RecyclerView.Adapter<BookCardAdaptor.RecyclerCardViewHolder> {

    private List<UserBook> bookList;
    private boolean isLibrary;


    public class RecyclerCardViewHolder extends RecyclerView.ViewHolder{
        public TextView bookTitle, authorName, progressText, isRead;
        public ImageView bookCover;
        public ProgressBar progressRead;
        public ConstraintLayout progressLayout;
        public RecyclerCardViewHolder(View vi){
            super(vi);
            this.bookTitle = vi.findViewById(R.id.book_title);
            this.authorName = vi.findViewById(R.id.author_name);
            this.bookCover = vi.findViewById(R.id.book_cover);
            this.isRead = vi.findViewById(R.id.is_read_text);
            this.progressText = vi.findViewById(R.id.progress_read_text);
            this.progressRead = vi.findViewById(R.id.progress_read);
            this.progressLayout = vi.findViewById(R.id.progress_layout);
        }
    }



    @Override
    public RecyclerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerCardViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_card, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerCardViewHolder holder, int position) {
        UserBook book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.authorName.setText(book.getAuthor());
        Glide
                .with(holder.itemView.getContext())
                .load(book.getCoverUri())
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.bookCover);
        if (book.isRead()) {
            holder.isRead.setVisibility(View.VISIBLE);
        } else {
            holder.isRead.setVisibility(View.GONE);
        }
        if (!isLibrary) {
            holder.progressLayout.setVisibility(View.GONE);
        } else {
            holder.progressRead.setMax((int)book.getPageCount());
            holder.progressRead.setProgress((int)book.getReadTo());
            holder.progressText.setText(book.getReadTo()+"/"+book.getPageCount());
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public BookCardAdaptor(Context context, List<UserBook> bookList, boolean isLibrary){
        Context context1 = context;
        this.bookList = bookList;
        this.isLibrary = isLibrary;
    }
}
