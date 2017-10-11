package vrpro.vrpro.util;

import java.text.DecimalFormat;

/**
 * Created by plooer on 8/29/2017 AD.
 */

public class DecimalUtil {
    public static String insertCommaDouble(Double temp){
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(temp);
    }

}
