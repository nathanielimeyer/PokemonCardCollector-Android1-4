package com.nathanielimeyer.pokemoncardcollector.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nathanielimeyer.pokemoncardcollector.Constants;
import com.nathanielimeyer.pokemoncardcollector.R;
import com.nathanielimeyer.pokemoncardcollector.models.Card;
import com.nathanielimeyer.pokemoncardcollector.ui.CardDetailActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FirebaseCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;
    public static final String TAG = FirebaseCardViewHolder.class.getSimpleName();


    View mView;
    Context mContext;

    public FirebaseCardViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindCard(Card card) {
        ImageView cardImageView = (ImageView) mView.findViewById(R.id.cardImageView);
        TextView nameTextView = (TextView) mView.findViewById(R.id.cardNameTextView);
        TextView pokemonTypeTextView = (TextView) mView.findViewById(R.id.pokemonTypeView);
        TextView hpTextView = (TextView) mView.findViewById(R.id.hpTextView);

        Picasso.with(mContext)
                .load(card.getImageUrl())
                .resize(MAX_WIDTH, MAX_HEIGHT)
                .centerCrop()
                .into(cardImageView);

        nameTextView.setText(card.getName());
        pokemonTypeTextView.setText(card.getTypes().get(0));
        hpTextView.setText("HP: " + card.getHp());
    }

    @Override
    public void onClick(View view) {
        final ArrayList<Card> cards = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_CARDS).child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cards.add(snapshot.getValue(Card.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, CardDetailActivity.class);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("cards", Parcels.wrap(cards));

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}