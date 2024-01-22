package com.apk.signature.Util;

import java.util.*;

class TreeNode {
    public char data;
    public boolean endOfWord;
    public Map<Character, TreeNode> children;
    public TreeNode parent;
    public TreeNode suffixLink;
    public TreeNode outputLink;

    public TreeNode(char ch, TreeNode parent) {
        this.data = ch;
        this.endOfWord = false;
        this.children = new HashMap<>();
        this.parent = parent;
        this.suffixLink = null;
        this.outputLink = null;
    }
}

class AhoCorasick {
    private final TreeNode root;

    public AhoCorasick() {
        root = new TreeNode('\0', null);
    }

    public void addWord(String word) {
        TreeNode current = root;

        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                TreeNode child = new TreeNode(ch, current);
                current.children.put(ch, child);
            }
            current = current.children.get(ch);
        }
        current.endOfWord = true;
    }

    public void buildSuffixAndOutputLinks() {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();

            // Build suffix link for each child of the current node
            for (TreeNode child : current.children.values()) {
                TreeNode temp = current.suffixLink;

                while (temp != null && !temp.children.containsKey(child.data)) {
                    temp = temp.suffixLink;
                }

                if (temp == null) {
                    child.suffixLink = root;
                } else {
                    child.suffixLink = temp.children.get(child.data);
                }

                queue.add(child);
            }

            // Build output link for each node
            if (current.suffixLink != null && current.suffixLink.endOfWord) {
                current.outputLink = current.suffixLink;
            } else if (current.suffixLink != null && current.suffixLink.outputLink != null) {
                current.outputLink = current.suffixLink.outputLink;
            }
        }
    }

    public List<String> searchWords(String text) {
        List<String> occurrences = new ArrayList<>();
        TreeNode current = root;

        for (char ch : text.toCharArray()) {
            while (current != null && !current.children.containsKey(ch)) {
                current = current.suffixLink;
            }

            if (current == null) {
                current = root;
            } else {
                current = current.children.get(ch);
            }

            if (current == null) {
                current = root;
            } else {
                TreeNode outputNode = current;

                while (outputNode != null) {
                    if (outputNode.endOfWord) {
                        occurrences.add(getWord(outputNode));
                    }
                    outputNode = outputNode.outputLink;
                }
            }
        }

        return occurrences;
    }

    private String getWord(TreeNode node) {
        Stack<Character> stack = new Stack<>();

        while (node.parent != null) {
            stack.push(node.data);
            node = node.parent;
        }

        StringBuilder sb = new StringBuilder();

        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        return sb.toString();
    }
}

public class AhuAlgorithm {
    public static void main(String[] args) {
        AhoCorasick ac = new AhoCorasick();
        ArrayList<String> words = new ArrayList<>();
       /* words.add("the");
        words.add("");
        words.add("he");
        words.add("she");
        words.add("his");
        words.add("hers");*/
        words.add("androidx.service.RemoAccServicd");

        for (String s : words) {
            ac.addWord(s);
        }

        ac.buildSuffixAndOutputLinks();

        // String text = "ushersandhersontheirsteps";
        String text = "androidx.service.RemoAccService";
        // String text = "ioii";
        List<String> occurrences = ac.searchWords(text);

        System.out.println("Occurrences: " + occurrences);
    }
}
