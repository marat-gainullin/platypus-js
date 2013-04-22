/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package opcda;

import com.eas.opc.hda.HistoryItem;
import com.eas.opc.hda.HistoryItem.History;
import com.eas.opc.hda.OPCHDAServerConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import org.jinterop.dcom.common.JIException;

/**
 *
 * @author pk
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JIException, UnknownHostException {
        final OPCHDAServerConnection conn = new OPCHDAServerConnection();
        conn.connect("Insat.TeplocomOpcVkt7.Hda", "mirex", "mirex", "opc", "zsh!987j");
        try{
//            final OPCDAServerConnection connda = new OPCDAServerConnection();
//            connda.connect("Insat.TeplocomOpcVkt7.Hda", "mirex", "mirex", "opc", "zsh!987j");
//            
            HistoryItem item1 = new HistoryItem("Суздальская-1-ВКТ-7.Все переменные.Архивные значения (сут\\.).Тепловой ввод 1.t1.Значение(число)", 1);
            HistoryItem item2 = new HistoryItem("Суздальская-1-ВКТ-7.Все переменные.Архивные значения (сут\\.).Тепловой ввод 2.t1.Значение(число)", 2);
            final int[] r1 = conn.addItems(item1, item2);
            System.out.println("Errors of adding: "+Arrays.toString(r1));
            System.out.println("serverHandles: "+item1.getServerHandle()+", "+item2.getServerHandle());
            final History[] histories = conn.readRaw(new Date(0), new Date(), 0, true, item1, item2);
            for (int i=0; i < histories.length; i++)
            {
                System.out.println("History for item "+histories[i].getItem()+" contains "+histories[i].getRecords().length+" records from "+histories[i].getStartTime()+" to "+histories[i].getEndTime());
                for (int j=0; j < histories[i].getRecords().length; j++)
                    System.out.println("   "+histories[i].getRecords()[j].getTimeStamp()+", quality="+histories[i].getRecords()[j].getQuality()+", value="+histories[i].getRecords()[j].getValue());
            }
        }finally{
            conn.disconnect();
        }
    }

}
