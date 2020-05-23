/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huongdoituong;
import huongdoituong.Person;
/**
 *
 * @author ADMIN
 */
public class Huongdoituong extends Person{
    protected Huongdoituong(String name, int age , float height){
        super (name, age , height);
    }
    public static void main(String[] args) {
        Huongdoituong a = new Huongdoituong("tien",19,1.7f);
        a.eat(" Rice");
        int age =a.getAge();
        System.out.println("His age:"+age);
        Huongdoituong b= new Huongdoituong("duy", 20, 1.76f);
    }
    
}

