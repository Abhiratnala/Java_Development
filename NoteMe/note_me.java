package notetakingapp;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class note_me {

    private static final String NOTEBOOKS_DIR = "notebooks" + File.separator;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new File(NOTEBOOKS_DIR).mkdirs(); // ensure storage folder exists

        while (true) {
            System.out.println("\n--- Note Taking Application ---");
            System.out.println("1. Create a new notebook");
            System.out.println("2. Open an existing notebook");
            System.out.println("3. Delete a notebook");
            System.out.println("4. Search notes");
            System.out.println("5. View all notebooks");
            System.out.println("6. Exit");
            System.out.print("Your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> createNotebook();
                case 2 -> openNotebook();
                case 3 -> deleteNotebook();
                case 4 -> searchNotes();
                case 5 -> listNotebooks();
                case 6 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void createNotebook() {
        System.out.print("Notebook name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        File file = new File(NOTEBOOKS_DIR + name + ".txt");
        if (file.exists()) {
            System.out.println("Notebook already exists. Open it? (yes/no)");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                notebookMenu(file);
            }
            return;
        }

        try {
            if (file.createNewFile()) {
                System.out.println("Created: " + name);
                notebookMenu(file);
            }
        } catch (IOException e) {
            System.out.println("Error creating notebook: " + e.getMessage());
        }
    }

    private static void openNotebook() {
        listNotebooks();
        System.out.print("Notebook name to open: ");
        File file = new File(NOTEBOOKS_DIR + scanner.nextLine().trim() + ".txt");

        if (file.exists()) {
            notebookMenu(file);
        } else {
            System.out.println("Notebook not found.");
        }
    }

    private static void deleteNotebook() {
        listNotebooks();
        System.out.print("Notebook name to delete: ");
        File file = new File(NOTEBOOKS_DIR + scanner.nextLine().trim() + ".txt");

        if (!file.exists()) {
            System.out.println("Notebook not found.");
            return;
        }

        System.out.print("Confirm delete (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes") && file.delete()) {
            System.out.println("Deleted successfully.");
        } else {
            System.out.println("Delete cancelled or failed.");
        }
    }

    private static void searchNotes() {
        listNotebooks();
        System.out.print("Notebook name to search: ");
        File file = new File(NOTEBOOKS_DIR + scanner.nextLine().trim() + ".txt");

        if (!file.exists()) {
            System.out.println("Notebook not found.");
            return;
        }

        System.out.print("Keyword: ");
        String keyword = scanner.nextLine().toLowerCase();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("\n--- Search Results ---");
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains(keyword)) {
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No matches found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading notebook: " + e.getMessage());
        }
    }

    private static void listNotebooks() {
        File[] notebooks = new File(NOTEBOOKS_DIR).listFiles((dir, name) -> name.endsWith(".txt"));
        if (notebooks == null || notebooks.length == 0) {
            System.out.println("No notebooks available.");
            return;
        }

        System.out.println("\n--- Available Notebooks ---");
        for (File notebook : notebooks) {
            System.out.println("- " + notebook.getName().replace(".txt", ""));
        }
    }

    private static void notebookMenu(File file) {
        while (true) {
            System.out.println("\n--- Notebook: " + file.getName().replace(".txt", "") + " ---");
            System.out.println("1. Write a note");
            System.out.println("2. Read all notes");
            System.out.println("3. Back to main menu");
            System.out.print("Your choice: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> writeNote(file);
                case 2 -> readNotes(file);
                case 3 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void writeNote(File file) {
        System.out.println("Enter note (type 'END' on a new line to finish):");
        StringBuilder content = new StringBuilder();
        String line;

        while (!(line = scanner.nextLine()).equals("END")) {
            content.append(line).append("\n");
        }

        if (content.length() == 0) {
            System.out.println("Empty note. Nothing saved.");
            return;
        }

        String timestamp = "--- " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " ---";
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(timestamp);
            out.println(content.toString().trim());
            System.out.println("Note saved.");
        } catch (IOException e) {
            System.out.println("Error writing note: " + e.getMessage());
        }
    }

    private static void readNotes(File file) {
        System.out.println("\n--- Notes ---");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean empty = true;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                empty = false;
            }
            if (empty) System.out.println("(No notes yet)");
        } catch (IOException e) {
            System.out.println("Error reading notes: " + e.getMessage());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                int num = Integer.parseInt(scanner.nextLine().trim());
                return num;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}
