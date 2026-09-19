package com.kiptoo2000.survey.bean;

public class LinkNode {

    LinkNode nextNode ;
     int data ;

    public LinkNode(LinkNode nextNode, int data) {
        this.nextNode = nextNode;
        this.data = data;
    }

    public void addList()
     {  LinkNode root = new LinkNode(nextNode ,5);
        nextNode =  new LinkNode(root,6);
        root =nextNode ;


     }

}
