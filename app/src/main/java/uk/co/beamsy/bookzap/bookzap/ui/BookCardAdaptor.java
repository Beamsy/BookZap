package uk.co.beamsy.bookzap.bookzap.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.R;

/**
 * Created by jake on 15/11/17.
 */

public class BookCardAdaptor extends RecyclerView.Adapter<BookCardAdaptor.RecyclerCardViewHolder> {

    private Context context;
    private List<Book> bookList;
    private boolean isLibrary;


    public class RecyclerCardViewHolder extends RecyclerView.ViewHolder{
        public TextView bookTitle, authorName, progressText, isRead;
        public ImageView bookCover;
        public ProgressBar progressRead;
        public ConstraintLayout progressLayout;
        public RecyclerCardViewHolder(View vi){
            super(vi);
            this.bookTitle = (TextView)vi.findViewById(R.id.book_title);
            this.authorName = (TextView)vi.findViewById(R.id.author_name);
            this.bookCover = (ImageView)vi.findViewById(R.id.book_cover);
            this.isRead = (TextView)vi.findViewById(R.id.is_read_text);
            this.progressText = (TextView)vi.findViewById(R.id.progress_read_text);
            this.progressRead = (ProgressBar)vi.findViewById(R.id.progress_read);
            this.progressLayout = (ConstraintLayout)vi.findViewById(R.id.progress_layout);
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
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.authorName.setText(book.getAuthor().authorName(null));
        Glide
                .with(holder.itemView.getContext())
                .load(book.getCoverUri())
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.bookCover);
        if (book.isRead()) {
            holder.isRead.setVisibility(View.VISIBLE);
        }
        if (!isLibrary) {
            holder.progressLayout.setVisibility(View.GONE);
        } else {
            holder.progressRead.setProgress(((book.getReadTo()/book.getPageCount())*100));
            holder.progressText.setText(book.getReadTo()+"/"+book.getPageCount());
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public BookCardAdaptor(Context context, List<Book> bookList, boolean isLibrary){
        this.context = context;
        this.bookList = bookList;
        this.isLibrary = isLibrary;
    }
}
