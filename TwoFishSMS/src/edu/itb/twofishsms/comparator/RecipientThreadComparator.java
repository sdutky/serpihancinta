package edu.itb.twofishsms.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import edu.itb.twofishsms.model.RecipientThread;

public class RecipientThreadComparator implements Comparator<Object>{
    @Override
    public int compare(Object obj1, Object obj2) {
    	RecipientThread sp1 = (RecipientThread) obj1; 
    	RecipientThread sp2 = (RecipientThread) obj2;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
			Date sp1Date = sdf.parse(sp1.getLastMessage().getModified());
			Date sp2Date = sdf.parse(sp2.getLastMessage().getModified());
			
			return (int)(sp2Date.getTime() - sp1Date.getTime());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return 0;
    } 
}
