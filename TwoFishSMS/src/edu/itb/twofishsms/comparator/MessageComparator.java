package edu.itb.twofishsms.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import edu.itb.twofishsms.provider.Message;

public class MessageComparator implements Comparator<Object>{
    @Override
    public int compare(Object obj1, Object obj2) {
        Message sp1 = (Message) obj1; Message sp2 = (Message) obj2;
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
			Date sp1Date = sdf.parse(sp1.getModified());
			Date sp2Date = sdf.parse(sp2.getModified());
			
			return (int)(sp1Date.getTime() - sp2Date.getTime());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
    	return 0;
        
    } 
}
