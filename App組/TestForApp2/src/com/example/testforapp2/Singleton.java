package com.example.testforapp2;

public class Singleton {
	
    private static Singleton _sharedInstance = null;
    
    /*****Variable************/
    
	/*************************/
    private Singleton(){//�p�� �����L�H�ŧi
        super();
    }
    public static Singleton getSharedInstance(){
        if(_sharedInstance == null){
        	_sharedInstance = new Singleton();
        }
        return _sharedInstance;
    }

    
    /*****Getting and Setting*****/
    
	/*****************************/
    
    
}
