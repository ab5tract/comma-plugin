package edument.perl6idea.profiler;

import edument.perl6idea.Perl6Icons;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Perl6ProfileTreeTable extends JXTreeTable {
    /* State of column may be: 1(sorted by this column),
     * 0(not sorted), -1(sorted by this column reversed).
     * Click on column `i` goes by this algorithm:
     * If column is 1  then set it to -1 and reverse
     * If column is 0  then set it to 1 and set all other values to 0
     * If column is -1 then set it to 1 and reverse
    */
    byte[] sorted = {0, 1, 0, 0};

    public Perl6ProfileTreeTable(TreeTableModel model) {
        super(model);
        getTableHeader().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = columnModel.getColumnIndexAtX(e.getX());
                if (index == -1) return;
                TreeTableModel model = getTreeTableModel();
                boolean reverse = false;
                switch (sorted[index]) {
                    case 1: {
                        sorted[index] = -1;
                        reverse = true;
                        break;
                    }
                    case 0: {
                        sorted = new byte[]{0, 0, 0, 0};
                        sorted[index] = 1;
                        break;
                    }
                    case -1: {
                        sorted[index] = 1;
                        reverse = true;
                        break;
                    }
                }
                if (model instanceof Perl6ProfileModel) {
                    ((Perl6ProfileModel)model).sortRoutines(index, reverse);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        // Set proper icons
        setLeafIcon(Perl6Icons.SUB);
        setCollapsedIcon(Perl6Icons.SUB);
        setExpandedIcon(Perl6Icons.SUB);
        setClosedIcon(Perl6Icons.SUB);
        setOpenIcon(Perl6Icons.SUB);
    }
}
