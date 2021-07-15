package searchEngine;

class BST {

    /*
    *This class sorts the links that have been sent to it into a binary search tree
    *Based on the bst generated we print out the links that have the most points(most data),
    *and the irrelevant links are stashed away. This is how the search engine becomes uniquely specific
    *in the results it displays.
    */

    public static Node<UrlData> createNode(UrlData k) {

    /*
    *The first is created. This is not necessarily the root node as the bst 
    *adjusts itself according to the points assigned to each link.
    */

        Node<UrlData> n = new Node<UrlData>();
        n.data = k;
        n.left = n.right = null;
        return n;
    }

    public static Node<UrlData> insert(Node<UrlData> node, UrlData val) {

        /*
        *If there is node in the bst the node is sent to the "createNode" class
        *Otherwise the nodes are set to make a bst based on their points.
        */

        if (node == null || node.data == null) {
            return (createNode(val));
        }

        if (val.points < node.data.points) {
            node.left = insert(node.left, val);
        } else if (val.points > node.data.points) {
            node.right = insert(node.right, val);
        }
        return node;
    }

}
