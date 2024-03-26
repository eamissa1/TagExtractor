import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TagExtractorGUI extends JFrame
{
    private JButton openTextFileButton = new JButton("Open Text File");
    private JButton openStopWordsFileButton = new JButton("Open Stop Words File");
    private JButton extractTagsButton = new JButton("Extract Tags");
    private JButton saveTagsButton = new JButton("Save Tags");
    private JTextArea tagsDisplayArea = new JTextArea();
    private File textFile;
    private File stopWordsFile;
    private Map<String, Integer> tagsFrequencyMap = new HashMap<>();

    public TagExtractorGUI()
    {
        setTitle("Tag/Keyword Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI()
    {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(openTextFileButton);
        topPanel.add(openStopWordsFileButton);
        topPanel.add(extractTagsButton);
        topPanel.add(saveTagsButton);

        add(topPanel, BorderLayout.NORTH);

        tagsDisplayArea.setEditable(false);
        add(new JScrollPane(tagsDisplayArea), BorderLayout.CENTER);

        openTextFileButton.addActionListener(this::openTextFile);
        openStopWordsFileButton.addActionListener(this::openStopWordsFile);
        extractTagsButton.addActionListener(e -> extractAndDisplayTags());
        saveTagsButton.addActionListener(this::saveTags);
    }

    private void openTextFile(ActionEvent e)
    {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            textFile = fileChooser.getSelectedFile();
        }
    }

    private void openStopWordsFile(ActionEvent e)
    {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            stopWordsFile = fileChooser.getSelectedFile();
        }
    }

    private void extractAndDisplayTags()
    {
        try {
            Set<String> stopWords = loadStopWords();
            tagsFrequencyMap.clear();

            try (Scanner scanner = new Scanner(textFile))
            {
                while (scanner.hasNext()) {
                    String word = scanner.next().toLowerCase().replaceAll("[^a-z]", "");
                    if (!stopWords.contains(word) && !word.isEmpty()) {
                        tagsFrequencyMap.put(word, tagsFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
            displayTags();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> loadStopWords() throws IOException
    {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        }
        return stopWords;
    }

    private void displayTags()
    {
        String tagsText = tagsFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));

        tagsDisplayArea.setText(tagsText);
    }

    private void saveTags(ActionEvent e)
    {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(new FileWriter(file)))
            {
                out.println(tagsDisplayArea.getText());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
