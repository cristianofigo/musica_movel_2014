import processing.net.*;


int i=0, op=1;
int rectX, rectX2, rectX3, rectX4, rectY; //Posição Botão
int rectSize = 35; // Tamanho Botão
color b1,b2,b3,b4; // Cor Botão
color corSom;
color fundo = color(150);
Client myClient;


boolean testarConexao(Client myClient){
 if (myClient.available()>0) 
 return true;
 else
 return false;
 }
  
void setup() {
  size(200, 200);
  background(fundo);
  
  
  open("/Users/cristianofigueiro/Downloads/trabalhofinal/Trabalho_Final_Pd.pd");
  delay(10000);
  frameRate(100);
  // criar um conexão
 // myServer = new Server(this, 3445);
 
  myClient = new Client(this, "127.0.0.1", 3445);
  if (testarConexao(myClient) == false){
      println("Conexao não estabelecida");
      testarConexao(myClient);
    }else
    println("Conectado");
      
  //myClientA = new Client(this, "127.0.0.1", 3445);
  noStroke();
  smooth();
  b1 = color(0,0,255);
  b2 = color(255,0,0);
  b3 = color(0,255,0);
  b4 = color(255);
  rectX = 5;
  rectX2= 55;
  rectX3= 105;
  rectX4= 155;
  rectY = 160;
  corSom=b1;

}

void draw() {
  //background(fundo);
  fill(b1);
  rect(rectX, rectY, rectSize, rectSize);
  fill(b2);
  rect(rectX2, rectY, rectSize, rectSize);
  fill(b3); 
  rect(rectX3, rectY, rectSize, rectSize);
  fill(b4);
  rect(rectX4, rectY, rectSize, rectSize);
  
  if(mousePressed){
  if(mouseX>=0 && mouseX<=width && mouseY>=0 && mouseY <=150){
    fill(corSom);
    ellipse(mouseX,mouseY,10,10); 
    myClient.write("Recebendo do Processing;");
    if(op==1)
    {
      myClient.write("SOM1 "+mouseX+";");
      myClient.write("VOL1 "+mouseY+";");
      
    }
    if(op==2)
    {
      myClient.write("SOM2 "+mouseX+";");
      myClient.write("VOL2 "+mouseY+";");
    }
    if(op==3)
    {
      myClient.write("SOM3 "+mouseX+";");
      myClient.write("VOL3 "+mouseY+";");
    }
    //if(op==4)
    //{
      //myClient.write("LIMPA 0;");
      //background(fundo);
    //}
  }
  }

  

}


void mousePressed() {
  
  if (mouseX >= rectX && mouseX <= rectX+rectSize && 
      mouseY >= rectY && mouseY <= rectY+rectSize) {
    corSom=b1;
    op=1;
  }
    if (mouseX >= rectX2 && mouseX <= rectX2+rectSize && 
      mouseY >= rectY && mouseY <= rectY+rectSize) {
    corSom=b2;
    op=2;
  }
    if (mouseX >= rectX3 && mouseX <= rectX3+rectSize && 
      mouseY >= rectY && mouseY <= rectY+rectSize) {
    corSom=b3;
    op=3;
  }
    if (mouseX >= rectX4 && mouseX <= rectX4+rectSize && 
      mouseY >= rectY && mouseY <= rectY+rectSize) {
    corSom=b4;
    //op=4;
    myClient.write("LIMPA 0;");
    background(fundo);
  }
}


