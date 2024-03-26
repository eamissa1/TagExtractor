import javax.swing.*;

public class TagExtractorRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            TagExtractorGUI frame = new TagExtractorGUI();
            frame.setVisible(true);
        });
    }
}
