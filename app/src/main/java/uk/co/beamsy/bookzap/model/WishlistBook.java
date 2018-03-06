package uk.co.beamsy.bookzap.model;

import android.net.Uri;

/**
 * Created by Jake on 06/03/2018.
 */

public class WishlistBook extends Book {

    private String amazonAffiliateString;
    private Uri amazonAffiliate;

    public WishlistBook () {

    }

    public String getAmazonAffiliateString() {
        return amazonAffiliateString;
    }

    public Uri getAmazonAffiliate() {
        if (amazonAffiliate == null) {
            amazonAffiliate = Uri.parse(amazonAffiliateString);
        }
        return amazonAffiliate;
    }

}
