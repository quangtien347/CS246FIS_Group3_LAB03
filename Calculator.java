package android.calculator;

import java.util.Arrays;
import java.util.Stack;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

class InfixToPostfix{
	boolean check_error = false;  // kiem tra ky tu dau tien la am hay duong, kiem tra loi
	
	public String standardizeDouble(double num){ //chuan hoa so 
		int a = (int)num;
		if (a == num) 
			return Integer.toString(a);
		else return Double.toString(num);
	}
	
	public boolean isCharPi(char c){ //kiem tra ky tu c la Pi hay khong
		if (c == 'π') return true;
		else return false;
	}
	
	public boolean isNumPi(double num){ //kiem tra so num la Pi hay khong
		if (num == Math.PI) return true;
		else return false;
	}
	
	public boolean isNum(char c){	//kiem tra ky tu c co la so khong (pi cung la so)
		if (Character.isDigit(c) || isCharPi(c)) return true;
		else return false;
	}
	
	public String NumToString(double num){ //chuyen so sang chuoi
		if (isNumPi(num)) return "π";
		else return standardizeDouble(num);
	}
	
	public double StringToNum(String s){ 	//Chuoi sang so
		if (isCharPi(s.charAt(0))) return Math.PI;
		else return Double.parseDouble(s);
	}
	
	public boolean isOperator(char c){ 	// kiem tra xem co phai toan tu
		char operator[] = { '+', '-', '*', '/', '^', '~', 's', 'c', 't', '@', '!', '%', ')', '('}; //~ thay cho dau am (-)
		Arrays.sort(operator);
		if (Arrays.binarySearch(operator, c) > -1)
			return true;
		else return false;
	}
	public int priority(char c){		// thiet lap thu tu uu tien
		switch (c) {
			case '+' : case '-' : return 1;
			case '*' : case '/' : return 2;
			case '~' : return 3;
			case '@' : case '!' : case '^' : return 4;
			case 's' : case 'c' : case 't' : return 5;
		}
		return 0;
	}
	
	public boolean isOneMath(char c){ 	// kiem tra toan tu 1 ngoi
		char operator[] = { 's', 'c', 't', '@', '('}; //~ thay cho dau am (-)
		Arrays.sort(operator);
		if (Arrays.binarySearch(operator, c) > -1)
			return true;
		else return false;
	}
	
	public String standardize(String s){ //chuan hoa bieu thuc
		String s1 = "";
		s = s.trim();
		s = s.replaceAll("\\s+"," "); //	chuan hoa s
		int open = 0, close = 0;
		for (int i=0; i<s.length(); i++){
			char c = s.charAt(i); 
			if (c == '(') open++;
			if (c == ')') close++;
		}
		for (int i=0; i< (open - close); i++) // them cac dau ")" vao cuoi neu thieu
			s += ')';
		for (int i=0; i<s.length(); i++){
        	if (i>0 && isOneMath(s.charAt(i)) && (s.charAt(i-1) == ')' || isNum(s.charAt(i-1)))) s1 = s1 + "*"; //	chuyen ...)(... thanh ...)*(...
        	if ((i == 0 || (i>0 && !isNum(s.charAt(i-1)))) && s.charAt(i) == '-' && isNum(s.charAt(i+1))) {
        		s1 = s1 + "~"; // check so am
        	}
        	/*else if ((i == 0 || (i>0 && !isNum(s.charAt(i-1)))) && s.charAt(i) == '+' && isNum(s.charAt(i+1))) {
        		s1 = s1 + ""; // check dau +
        	}*/
        		else if (i>0 && (isNum(s.charAt(i-1)) || s.charAt(i-1) == ')') && isCharPi(s.charAt(i))) s1 = s1 + "*" + s.charAt(i);
    			// VD hoac 6π , ...)π chuyen sang 6*π , ...)*π 
        			else s1 = s1 + s.charAt(i);
        }
		return s1;
	}
	
	public String[] processString(String sMath){ // xu ly bieu thuc nhap vao thanh cac phan tu
		String s1 = "", elementMath[] = null;
		sMath = standardize(sMath);
		InfixToPostfix  ITP = new InfixToPostfix();
		for (int i=0; i<sMath.length(); i++){
			char c = sMath.charAt(i);
			if (i<sMath.length()-1 && isCharPi(c) && !ITP.isOperator(sMath.charAt(i+1))){ // error neu co dang π3
				check_error = true;
				return null;
			}
			else 
				if (!ITP.isOperator(c))
					s1 = s1 + c;
				else s1 = s1 + " " + c + " ";
		}
		s1 = s1.trim();
		s1 = s1.replaceAll("\\s+"," "); //	chuan hoa s1
		elementMath = s1.split(" "); //tach s1 thanh cac phan tu
		return elementMath;
	}
	
	public String[] postfix(String[] elementMath){
		InfixToPostfix  ITP = new InfixToPostfix();
		String s1 = "", E[];
		Stack <String> S = new Stack<String>();
		for (int i=0; i<elementMath.length; i++){ 	// duyet cac phan tu
			char c = elementMath[i].charAt(0);		// c la ky tu dau tien cua moi phan tu
			
			if (!ITP.isOperator(c)) 				// neu c khong la toan tu 
				s1 = s1 + elementMath[i] + " ";		// xuat elem vao s1
			else{									// c la toan tu
				if (c == '(') S.push(elementMath[i]);	// c la "(" -> day phan tu vao Stack
				else{ 									
					if (c == ')'){						// c la ")"
						char c1;						//duyet lai cac phan tu trong Stack
						do{
							c1 = S.peek().charAt(0);	// c1 la ky tu dau tien cua phan tu
							if (c1 != '(') s1 = s1 + S.peek() + " "; 	// trong khi c1 != "("
							S.pop();
						}while (c1 != '(');
					}
					else{
						// Stack khong rong va trong khi phan tu trong Stack co do uu tien >= phan tu hien tai
						while (!S.isEmpty() && ITP.priority(S.peek().charAt(0)) >= ITP.priority(c)) 
							s1 = s1 + S.pop() + " ";
						S.push(elementMath[i]); // 	dua phan tu hien tai vao Stack
					}
				}
			}
		}
		while (!S.isEmpty()) s1 = s1 + S.pop() + " "; // Neu Stack con phan tu thi day het vao s1
		E = s1.split(" ");	//	tach s1 thanh cac phan tu 
		return E;
	}
	
	public String valueMath(String[] elementMath){
		Stack <Double> S = new Stack<Double>();
		InfixToPostfix  ITP = new InfixToPostfix();
		double num = 0.0;
		for (int i=0; i<elementMath.length; i++){
			char c = elementMath[i].charAt(0);	
			if (isCharPi(c)) S.push(Math.PI);	// neu la pi
			else{
				if (!ITP.isOperator(c)) S.push(Double.parseDouble(elementMath[i])); //so
				else{	// toan tu
					
					double num1 = S.pop();
					switch (c) {
						case '~' : num = -num1; break;
						case 's' : num = Math.sin(num1); break;
						case 'c' : num = Math.cos(num1); break;
						case 't' : num = Math.tan(num1); break; 
						case '%' : num = num1/100; break;
						case '@' : {
							if (num1 >=0){
								num = Math.sqrt(num1); break;
							}
							else check_error = true;
						}
						case '!' : {
							if (num1 >= 0 && (int)num1 == num1){
								num = 1;
								for (int j=1; j<=(int)num1; j++)
									num = num * j;
							}
							else check_error = true;
						}
						default : break;
					}
					if (!S.empty()){
						double num2 = S.peek();
						switch (c) {
						//-----------------------
							case '+' : num = num2 + num1; S.pop(); break;
							case '-' : num = num2 - num1; S.pop(); break;
							case '*' : num = num2 * num1; S.pop(); break;
							case '/' : {
								if (num1 != 0) num = num2 / num1;
								else check_error = true;
								S.pop(); break;
							}
							case '^' : num = Math.pow(num2, num1); S.pop(); break;							
						}
					}
					S.push(num);
				}			
			}
		}
		return NumToString(S.pop());
	}
}
public class Calculator extends Activity implements OnClickListener{
	String textMath = "", textAns = "0", screenTextMath = "";
	double num1 = 0, num2 = 0, ans = 0;
	char dau = ' ';
	int checkSubmit = 0;
	TextView screenAns, screenMath;
	Button bt0, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, btDot, btPi, btAdd, btMinus, btMultiply, btDived, btSqrt, 
	btPercent, btInverse, btSubmit, btReset, btClear, btBack, btOpen, btClose,btSin, btCosin, btTan, btPow, 
	btFactorial, btAbout, btHelp;
	
	/*Add : cong, Minus :tru, Multiply: nhan, Dived : chia, Percent : phan tram, Sqrt: can, inverse : nghich dao,
	 Submit : gui, Facetorial giai thua*/
	
	public void error(){
		screenAns.setText("Math Error !");
		textAns = textMath = screenTextMath = "";
	}
	
	public void submit(String[] elementMath){
		InfixToPostfix  ITP = new InfixToPostfix();
		if (textMath.length()>0){
			try{
				if (!ITP.check_error) elementMath = ITP.processString(textMath);		//	tach bieu thuc thanh cac phan tu
				if (!ITP.check_error) elementMath = ITP.postfix(elementMath);		// 	dua cac phan tu ve dang postfix
				if (!ITP.check_error) textAns = ITP.valueMath(elementMath);		//lay gia tri
				screenAns.setText(textAns);
				textMath = textAns;
				screenTextMath = textAns;
				checkSubmit = 1;	
			}catch(Exception e){
				error();
			}
			if (ITP.check_error) error();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);
		
		screenAns = (TextView) findViewById(R.id.screenAns);
		screenMath = (TextView) findViewById(R.id.screenMath);
		
		bt0 = (Button) findViewById(R.id.num0);
		bt0.setOnClickListener(this);
		
		bt1 = (Button) findViewById(R.id.num1);
		bt1.setOnClickListener(this);
		
		bt2 = (Button) findViewById(R.id.num2);
		bt2.setOnClickListener(this);
		
		bt3 = (Button) findViewById(R.id.num3);
		bt3.setOnClickListener(this);
		
		bt4 = (Button) findViewById(R.id.num4);
		bt4.setOnClickListener(this);
		
		bt5 = (Button) findViewById(R.id.num5);
		bt5.setOnClickListener(this);
		
		bt6 = (Button) findViewById(R.id.num6);
		bt6.setOnClickListener(this);
		
		bt7 = (Button) findViewById(R.id.num7);
		bt7.setOnClickListener(this);
		
		bt8 = (Button) findViewById(R.id.num8);
		bt8.setOnClickListener(this);
		
		bt9 = (Button) findViewById(R.id.num9);
		bt9.setOnClickListener(this);
		
		btDot = (Button) findViewById(R.id.dot);
		btDot.setOnClickListener(this);
		
		btPi = (Button) findViewById(R.id.pi);
		btPi.setOnClickListener(this);
		
		btAdd = (Button) findViewById(R.id.add);
		btAdd.setOnClickListener(this);
		
		btMinus = (Button) findViewById(R.id.minus);
		btMinus.setOnClickListener(this);
		
		btSubmit = (Button) findViewById(R.id.submit);
		btSubmit.setOnClickListener(this);
		
		btMultiply = (Button) findViewById(R.id.multiply);
		btMultiply.setOnClickListener(this);
		
		btDived = (Button) findViewById(R.id.dived);
		btDived.setOnClickListener(this);
		
		btPercent = (Button) findViewById(R.id.percent);
		btPercent.setOnClickListener(this);
		
		btSqrt = (Button) findViewById(R.id.sqrt);
		btSqrt.setOnClickListener(this);
		
		btInverse = (Button) findViewById(R.id.inverse);
		btInverse.setOnClickListener(this);
		
		btOpen = (Button) findViewById(R.id.open);
		btOpen.setOnClickListener(this);
		
		btClose = (Button) findViewById(R.id.close);
		btClose.setOnClickListener(this);
		
		btSin = (Button) findViewById(R.id.sin);
		btSin.setOnClickListener(this);
		
		btCosin = (Button) findViewById(R.id.cosin);
		btCosin.setOnClickListener(this);
		
		btTan = (Button) findViewById(R.id.tan);
		btTan.setOnClickListener(this);
		
		btFactorial = (Button) findViewById(R.id.factorial);
		btFactorial.setOnClickListener(this);
		
		btPow = (Button) findViewById(R.id.pow);
		btPow.setOnClickListener(this);
		
		btReset = (Button) findViewById(R.id.reset);
		btReset.setOnClickListener(this);
		
		btReset = (Button) findViewById(R.id.reset);
		btReset.setOnClickListener(this);
		
		btClear = (Button) findViewById(R.id.clear);
		btClear.setOnClickListener(this);	
		
		btAbout = (Button) findViewById(R.id.about);
		btAbout.setOnClickListener(this);
		
		btHelp = (Button) findViewById(R.id.help);
		btHelp.setOnClickListener(this);
	}



