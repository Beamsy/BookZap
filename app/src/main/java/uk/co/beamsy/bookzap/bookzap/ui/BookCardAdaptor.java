package uk.co.beamsy.bookzap.bookzap.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.beamsy.bookzap.bookzap.model.Book;
import uk.co.beamsy.bookzap.bookzap.R;

/**
 * Created by jake on 15/11/17.
 */

public class BookCardAdaptor extends RecyclerView.Adapter<BookCardAdaptor.RecyclerCardViewHolder> {

    private Context context;
    private List<Book> bookList;


    public class RecyclerCardViewHolder extends RecyclerView.ViewHolder{
        public TextView bookTitle, authorName;
        public ImageView bookCover;
        public RecyclerCardViewHolder(View vi){
            super(vi);
            this.bookTitle = (TextView)vi.findViewById(R.id.book_title);
            this.authorName = (TextView)vi.findViewById(R.id.author_name);
            this.bookCover = (ImageView)vi.findViewById(R.id.book_cover);
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
        holder.authorName.setText(book.getAuthor().getName());
        holder.bookCover.setImageBitmap(BitmapFactory.decodeResource(holder.bookTitle.getContext().getResources(), book.getCoverId()));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }



    public BookCardAdaptor(Context _context, List<Book> _bookList){
        this.context = _context;
        this.bookList = _bookList;
    }
}
