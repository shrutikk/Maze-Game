package maze;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import maze.Maz_Prims_Astar.Option;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Maze_Prims_Djikstra extends PApplet {
	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "maze.Maze_Prims_Djikstra" });
	}
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int x1 = (int) screenSize.getWidth(), y1 = (int) (0.95 * screenSize.getHeight()), y2 = (int) screenSize.getHeight();
	int count, count1 = 0;
	int xTile, yTile, xd, yd;
	int size = 80;
	int[] tileX = new int[x1 / size];
	int[] tileY = new int[y1 / size];
	int row, col;
	PVector destination = new PVector(10, 10);
	int Start, Goal;
	int radius = 10, directionX = 0, directionY = 0;
	Character character,bot;
	float radiusofsat = (float) 1;
	float radiusofdecel = (float) 0;
	float ofinal;
	int z = 1;
	PVector mouse;
	PShape arrow,cockroach,finishPoint;
	Node currentNode, destNode;
	boolean hint=false;
	int array[][] = new int[tileX.length * tileY.length][2];
	Node[] NodeAry = new Node[array.length - count];
	Connection[] con;// =new Connection[connectionArray.length];
	ArrayList<Connection> conList = new ArrayList<Connection>();
	Stack<Node> st = new Stack<Node>();
	ArrayList<Node> visited = new ArrayList<Node>();
	CreateConnection c = new CreateConnection();
	Arrive playerArrive,botArrive;
	Astar playerPath,botPath;
	int i =1,initialBotPosition=5,pathIndex=1;
	int changeCounter = 1, startTime;
	boolean gameOver = false,changed=false,botPresent=true;
	float totalDistance, distTillNow;
	PImage img;
	int userChanged=0,hintcount=-1;
	public void settings() {
		size(x1, y2);
		img = loadImage("Maze.JPG");
	}
	public void draw() {
		if(startGame==false){
			welcomePage();
		}else{
		background(00);
		stroke(255);
		fill(255);
		strokeWeight((int) (size * 0.75));
		fill(255);

		printConnection();
		if (!gameOver && !reachedGoal()) {

			if (remainingDistance() < totalDistance/2 && !changed) {
				createNodeArray();
				createNodes();
				updateNodeData();
				conList = new ArrayList<Connection>();
				c.Prims();
				createConnection();
				printConnection();
				currentNode = NodeAry[currentNode.nodeNo];
				destNode = NodeAry[destNode.nodeNo];
				
				changed=true;
			}
			if(hintcount>=0 && hintcount<30){
				playerPath.showPath(false);
				hintcount++;
			}
			 
			arrow = loadShape("trollHappy.svg");
			cockroach = loadShape("cockroach.svg");
			finishPoint=loadShape("Flag.svg");
			shapeMode(CENTER);
			character.target = mouse;
			
			
			playerArrive.update();
			playerArrive.checkEdges();
			if(botPresent){
				if (pathIndex < botPath.path.size())
					bot.target = new PVector(NodeAry[botPath.path.get(pathIndex).nodeNo].x,
							NodeAry[botPath.path.get(pathIndex).nodeNo].y);
				botArrive.update();
			}
			score();
		}
		
		shape(finishPoint, NodeAry[Goal].x, NodeAry[Goal].y, size, size);
		shape(arrow, (float) character.getx(), (float) character.gety(), character.getlength(), character.getheight());
		if(botPresent){
			shape(cockroach, (float) bot.getx(), (float) bot.gety(), bot.getlength(), bot.getheight());
			if (bot.location.dist(character.location) < 10) {
				fill(255, 0, 0);
				textSize(y1/10);
				textAlign(CENTER);
				text("Game Over",(int)(x1*0.50),(int)(y2*0.5));
				text("Your Time :"+time / 100 + ":" + time % 100,(int)(x1*0.50),(int)(y2*0.6));
				gameOver = true;
			}
		}
		if (reachedGoal()) {
			fill(255, 0, 0);
			textSize(y1/10);
			textAlign(CENTER);
			text("You win",(int)(x1*0.50),(int)(y2*0.5));
			text("Your Time :"+time / 100 + ":" + time % 100,(int)(x1*0.50),(int)(y2*0.6));
		}
		changeCounter++;
		}
	}

	public void allSetting(){
		size(x1, y2);
		tileX = new int[x1 / size];
		tileY = new int[y1 / size];
		array = new int[tileX.length * tileY.length][2];
		NodeAry = new Node[array.length - count];
		createNodeArray();
		createNodes();
		updateNodeData();
		c.Prims();
		createConnection();
		Start = 0;
		Goal = NodeAry.length - 1;
		
		playerPath = new Astar();
		botPath = new Astar();
		playerPath.findPath(NodeAry[Start], NodeAry[Goal],0,NodeAry.length-1);
		clearNOdeHistory();
		currentNode = NodeAry[Start];
		destNode = NodeAry[Start];
		initialBotPosition = (col)*((int)(row*0.4))-1;
		if(botPresent){
			botPath.findPath(NodeAry[initialBotPosition], currentNode,initialBotPosition,currentNode.nodeNo);
			bot = new Character(NodeAry[initialBotPosition].x, NodeAry[initialBotPosition].y, 0, 0, (int)(size * 0.70), (int)(size * 0.50),true);
			botArrive= new Arrive(bot,3,2,10);
		}
				
		character = new Character(NodeAry[Start].x, NodeAry[Start].y, 0, 0, (int)(size * 0.70), (int)(size * 0.50),false);
		mouse = new PVector(NodeAry[Start].x, NodeAry[Start].y);
		playerArrive= new Arrive(character,3,2,10);
		startTime = millis();
		PVector startp = new PVector(NodeAry[Start].x, NodeAry[Start].y);
		PVector endp = new PVector(NodeAry[Goal].x, NodeAry[Goal].y);
		totalDistance = startp.dist(endp);
	}
	boolean startGame;
	int mazeSize=x1/25;
	public void welcomePage(){
		Imageposition=(x1/img.width)*img.height;
		background(100);
		imageMode(CORNERS);
		image(img,0, 0,x1, Imageposition); 
		textSize((int)(y1/15));
		fill(0);
		text("Press S to Start Game",(int)(x1*0.35),(int)(y2*0.9));
		
		opLevel1.createOpt();
		opLevel1.createOptions("Easy");
		opLevel2.createOpt();
		if(!opLevel1.state && !opLevel2.state && !opLevel3.state && !opLevel4.state){
		opLevel2.state=true;
		}
		opLevel2.createOptions("Medium");
		opLevel3.createOpt();
		opLevel3.createOptions("Hard");
		opLevel4.createOpt();
		opLevel4.createOptions("Insane");
		modePractice.createOpt();
		modePractice.createOptions("Practice");
		if(!modeChallenge.state && !modePractice.state){
			modeChallenge.state=true;
			}
		modeChallenge.createOpt();
		modeChallenge.createOptions("Challenge");
		if(key=='s'){
			startGame=true;
			startTime=millis();
			if(opLevel1.state){
//				size=100;
				size=(int)x1/200;
				size=size*10;
			}
			else if(opLevel2.state){
				size=(int)x1/230;
				size=size*10;
//				size=80;
			}
			else if(opLevel3.state){
				size=(int)x1/290;
				size=size*10;
//				size=50;
			}
			else if(opLevel4.state){
				size=(int)x1/350;
				size=size*10;
//				size=50;
			}
			else{
//				opLevel2.state=true;
				size=(int)x1/250;
				size=size*10;
			}
//			size=80;
//			System.out.println(size);
			allSetting();
		}
	}
	float circleSize=(float)(y1/20);
	public class Option{
		float bx,by;
		boolean selectC=false;
		boolean selected=false;
		boolean state=false;
		int color;
		public Option(float ay){
			bx=x1*0.4f;
			by=y2*ay*0.9f;
		}
		public void createOptions(String mode){
			textSize((int)(y1/15));
			fill(0);
			text(mode,bx+circleSize,by+circleSize/3);
			color=0;
		}
		public void createOpt(){
				if((dist(bx,by,mouseX,mouseY)<=circleSize/2)||state==true){
					color=255;
					selected=true;
				}
				else{
					color=0;
					selected=false;
				}
//			}
			fill(color);
			ellipse(bx,by,circleSize,circleSize);
		}
	}
	int Imageposition;//=(x1/img.width)*img.height;
	Option modePractice=new Option(0.48f);
	Option modeChallenge=new Option(0.55f);
	Option opLevel1=new Option(0.66f);
	Option opLevel2=new Option(0.74f);
	Option opLevel3=new Option(0.82f);
	Option opLevel4=new Option(0.90f);
	public void mouseClicked(){
		if(dist(opLevel1.bx,opLevel1.by,mouseX,mouseY)<circleSize/2){
			opLevel1.state=true;
			opLevel2.state=false;
			opLevel3.state=false;
			opLevel4.state=false;
		}
		else if(dist(opLevel2.bx,opLevel2.by,mouseX,mouseY)<circleSize/2){
			opLevel1.state=false;
			opLevel2.state=true;
			opLevel3.state=false;
			opLevel4.state=false;
		}
		else if(dist(opLevel3.bx,opLevel3.by,mouseX,mouseY)<circleSize/2){
			opLevel1.state=false;
			opLevel2.state=false;
			opLevel3.state=true;
			opLevel4.state=false;
		}
		else if(dist(opLevel4.bx,opLevel4.by,mouseX,mouseY)<circleSize/2){
			opLevel1.state=false;
			opLevel2.state=false;
			opLevel3.state=false;
			opLevel4.state=true;
		}
		else{
			
		}
//		System.out.println("mazeSize :"+mazeSize);
		if(dist(modePractice.bx,modePractice.by,mouseX,mouseY)<circleSize/2){
			modePractice.state=true;
			modeChallenge.state=false;
			botPresent=false;
		}
		else if(dist(modeChallenge.bx,modeChallenge.by,mouseX,mouseY)<circleSize/2){
			modePractice.state=false;
			modeChallenge.state=true;
			botPresent=true;
		}
	}



	Node X = new Node(30, 30);

	public float remainingDistance() {
		PVector currentp = new PVector(currentNode.x, currentNode.y);
		PVector endp = new PVector(NodeAry[Goal].x, NodeAry[Goal].y);
		return currentp.dist(endp);
	}
	int time;
	public void score() {
		fill(255, 0, 0);
		textSize((int) (size * 0.75)); //
		int timer = (millis() - startTime) / 10;
		text("Time :" + timer / 100 + ":" + timer % 100, (int) (size * 0.5), NodeAry[row * col - 1].y + size);
		time =timer;
	}

	public boolean reachedGoal() {

		if (character.location.dist(new PVector(NodeAry[Goal].x, NodeAry[Goal].y)) < 1)
			return true;
		return false;
	}
	public void clearNOdeHistory(){
		for(int i=0;i<NodeAry.length;i++){
			NodeAry[i].costSoFar=0;
			NodeAry[i].estimatedTotalCost=0;
		}
	}
	public void createNodeArray() {
		int p = 0;
		int q = 0;
		int k = 0;
		while (k < tileX.length) {
			tileX[k] = p + size / 2;
			p += size;
			k++;
		}
		k = 0;
		while (k < tileY.length) {
			tileY[k] = q + size / 2;
			q += size;
			k++;
		}
		int z = 0;
		while (z < array.length) {
			k = 0;
			while (k < tileY.length) {
				int m = 0;
				while (m < tileX.length) {
					array[z][0] = tileX[m];
					array[z][1] = tileX[k];
					m++;
					z++;
				}
				k++;
				col = m;
			}
			row = k;
			break;
		}
	}

	
	public void createNodes() {
		NodeAry = new Node[array.length - count];
		int j = 0, a = 0;
		for (int i = 0; i < array.length - a; i++) {
			j = i + a;
			NodeAry[i] = new Node(array[j][0], array[j][1]);
			NodeAry[i].nodeNo = i;
			NodeAry[i].ar = j;
		}
		// System.out.println(array.length);
		NodeAry = Arrays.copyOf(NodeAry, NodeAry.length - a);
	}

	public void printNodes() {
		int k = 0;
		while (k < NodeAry.length) {
			ellipse(NodeAry[k].x, NodeAry[k].y, 6, 6);
			k++;
		}
	}

	public class Node {
		int x, y, nodeNo, ar;
		float costSoFar, heuristic, estimatedTotalCost;
		Connection fromCon;
		boolean top, bot, right, left;
		int nearestCon = 10000; // up,down,left,right
		Node topNode, botNode, leftNode, rightNode;

		// Connection topCon,botCon,leftCon,rightCon;
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
			costSoFar = 0;
		}

		public void calculateHeurestic() {
			heuristic = dist(NodeAry[Goal].x, NodeAry[Goal].y, x, y);
			// heuristic=Math.abs(NodeAry[Goal].x-x)+
			// Math.abs(NodeAry[Goal].y-y);
		}
	}

	public void getNearestNodes(Node N) {
		if (N.nodeNo - col < 0) {
			N.topNode = null;
			N.botNode = NodeAry[N.nodeNo + col];
		} else if (N.nodeNo + col >= (NodeAry.length)) {
			N.botNode = null;
			N.topNode = NodeAry[N.nodeNo - col];
		} else {
			N.topNode = NodeAry[N.nodeNo - col];
			N.botNode = NodeAry[N.nodeNo + col];
		}
		if (N.nodeNo % col == 0) {
			N.leftNode = null;
			N.rightNode = NodeAry[N.nodeNo + 1];
		} else if (N.nodeNo % col == (col - 1)) {
			N.rightNode = null;
			N.leftNode = NodeAry[N.nodeNo - 1];
		} else {
			N.rightNode = NodeAry[N.nodeNo + 1];
			N.leftNode = NodeAry[N.nodeNo - 1];
		}
	}

	public void updateNodeData() {
		for (int i = 0; i < NodeAry.length; i++) {
			getNearestNodes(NodeAry[i]);
			/*
			 * System.out.print(i); if(NodeAry[i].topNode!=null){
			 * System.out.print(" T:"+NodeAry[i].topNode.nodeNo); }
			 * if(NodeAry[i].botNode!=null){ System.out.print(" B:"
			 * +NodeAry[i].botNode.nodeNo); } if(NodeAry[i].leftNode!=null){
			 * System.out.print(" L:"+NodeAry[i].leftNode.nodeNo); }
			 * if(NodeAry[i].rightNode!=null){ System.out.print(" R:"
			 * +NodeAry[i].rightNode.nodeNo); } System.out.println();
			 */
		}
	}


	public class CreateConnection {
		Random randomizer = new Random();
		int check = 0;

		public void Prims() {
			List<Node> inset = new ArrayList<Node>();

			inset.add(NodeAry[0]);
			while (inset.size() != NodeAry.length) {
				selectRandomNode(inset);
			}
		}

		public void selectRandomNode(List<Node> inset) {

			Node randomNode, randomNode2, randomNode3;
			List<Node> frontierList = new ArrayList<Node>();
			List<Node> loopList = new ArrayList<Node>();
			randomNode = inset.get(randomizer.nextInt(inset.size()));
			frontierList = possibleNeighbour(randomNode, inset);
			if (frontierList.size() > 0) {
				randomNode2 = frontierList.get(randomizer.nextInt(frontierList.size()));
				inset.add(randomNode2);
				if (randomNode2 == randomNode.leftNode) {
					randomNode.left = true;
					randomNode2.right = true;
				} else if (randomNode2 == randomNode.rightNode) {
					randomNode.right = true;
					randomNode2.left = true;
				} else if (randomNode2 == randomNode.topNode) {
					randomNode.top = true;
					randomNode2.bot = true;
				} else if (randomNode2 == randomNode.botNode) {
					randomNode.bot = true;
					randomNode2.top = true;
				}

				Connection c = new Connection(randomNode, randomNode2);
				Connection c1 = new Connection(randomNode2, randomNode);
				conList.add(c);
				conList.add(c1);
				if (randomNode.nodeNo % 5 == 0) {
					loopList = getAllNeighbours(randomNode);
					randomNode3 = loopList.get(randomizer.nextInt(loopList.size()));
					while (randomNode3.nodeNo == randomNode2.nodeNo) {
						randomNode3 = loopList.get(randomizer.nextInt(loopList.size()));
					}
					if (randomNode3 == randomNode.leftNode) {
						randomNode.left = true;
						randomNode3.right = true;
					} else if (randomNode3 == randomNode.rightNode) {
						randomNode.right = true;
						randomNode3.left = true;
					} else if (randomNode3 == randomNode.topNode) {
						randomNode.top = true;
						randomNode3.bot = true;
					} else if (randomNode3 == randomNode.botNode) {
						randomNode.bot = true;
						randomNode3.top = true;
					}

					c = new Connection(randomNode, randomNode3);
					c1 = new Connection(randomNode3, randomNode);
					conList.add(c);
					conList.add(c1);
				}

			}
		}

		public List<Node> getAllNeighbours(Node N) {
			List<Node> nbr = new ArrayList<Node>();
			if (N.leftNode != null) {
				nbr.add(N.leftNode);
			}
			if (N.rightNode != null) {
				nbr.add(N.rightNode);
			}
			if (N.topNode != null) {
				nbr.add(N.topNode);
			}
			if (N.botNode != null) {
				nbr.add(N.botNode);
			}
			return nbr;
		}

		public ArrayList possibleNeighbour(Node N, List<Node> inset) {
			ArrayList<Node> list = new ArrayList<Node>();
			if (N.leftNode != null && !inset.contains(N.leftNode)) {
				list.add(N.leftNode);
			}
			if (N.rightNode != null && !inset.contains(N.rightNode)) {
				list.add(N.rightNode);
			}
			if (N.topNode != null && !inset.contains(N.topNode)) {
				list.add(N.topNode);
			}
			if (N.botNode != null && !inset.contains(N.botNode)) {
				list.add(N.botNode);
			}
			return list;
		}
	}

	public class Connection {
		float cost = 0, conNo;
		Node fromNode, toNode;

		public Connection(Node X, Node Y) {
			cost = dist(X.x, X.y, Y.x, Y.y);
			// cost = random(50,5000);
			fromNode = X;
			toNode = Y;
		}

		public void drawLine() {
			line(fromNode.x, fromNode.y, toNode.x, toNode.y);
		}
	}

	public void createConnection() {
		ListIterator<Connection> itrCon = conList.listIterator();
		int i = 0;
		con = new Connection[conList.size()];
		while (itrCon.hasNext()) {
			Connection c = itrCon.next();
			con[i] = c;
			i++;
		}
	}

	public void printConnection() {
		ListIterator<Connection> itrCon = conList.listIterator();
		//			int i = 0;
		/*for(int i=0;i<con.length;i++){
				line(con[i].fromNode.x, con[i].fromNode.y, con[i].toNode.x, con[i].toNode.y);
				i++;
			}*/
		while (itrCon.hasNext()) {
			//itrCon.next();
			Connection c = itrCon.next();
			line(c.fromNode.x, c.fromNode.y, c.toNode.x, c.toNode.y);
		}
	}
	
	public void addHeuristics() {
		for (int i = 0; i < NodeAry.length; i++) {
			NodeAry[i].calculateHeurestic();
		}
	}
	
	public class Astar{
		ArrayList<Node> openNodes;// =new ArrayList<Node>();
		ArrayList<Node> closedNodes;// =new ArrayList<Node>();
		ArrayList<Node> unvisitedNodes;
		ArrayList<Node> path;
		int start,goal;
		
		
		public void findPath(Node Start, Node Goal,int st,int gl) {
			start = st;
			goal = gl;
			openNodes = new ArrayList<Node>();
			closedNodes = new ArrayList<Node>();
			unvisitedNodes = new ArrayList<Node>(Arrays.asList(NodeAry));
			int count = 0;
			while (Start != Goal) {
				Start = closeNode(Start, Goal);
				count++;
			}
			path = getPath();
			Collections.reverse(path);
			
		}
		
		public Node closeNode(Node N, Node Goal) {
			if (N == NodeAry[start]) {
				N.fromCon=null;
				N.costSoFar = 0;
			} else {
				N.costSoFar = N.fromCon.cost + N.fromCon.fromNode.costSoFar;
			}
			N.estimatedTotalCost = N.costSoFar + N.heuristic;
			if (unvisitedNodes.contains(N)) {
				unvisitedNodes.remove(N);
			}
			return addNodesToOpen(N, Goal);
		}
		
		public Node addNodesToOpen(Node N, Node Goal) {// need to consider middle
			// point as goal(incomings)
			for (int i = 0; i < con.length; i++) {
				// System.out.print(con[i].fromNode.nodeNo+",");
				if (con[i].fromNode == N) {// second end means not traversed that
					// node
					if (openNodes.contains(con[i].toNode) || closedNodes.contains(con[i].toNode)) {
						float costOfNode = con[i].cost + N.costSoFar;
						if (con[i].toNode.costSoFar > costOfNode) {
							con[i].toNode.costSoFar = costOfNode;
							con[i].toNode.fromCon = con[i];
							con[i].toNode.estimatedTotalCost = costOfNode + con[i].toNode.heuristic;
						}
					} else {
						con[i].toNode.costSoFar = con[i].cost + N.costSoFar;
						con[i].toNode.estimatedTotalCost = con[i].toNode.costSoFar + con[i].toNode.heuristic;
						openNodes.add(con[i].toNode);
						unvisitedNodes.remove(con[i].toNode);
						con[i].toNode.fromCon = con[i];
					}
				}
			}
			if (openNodes.isEmpty()) {
				return Goal;
			} else {
				Node X = findTotalMinimumEstimatedCostNodeSoFar();// and close it
				closedNodes.add(N);
				if (openNodes.contains(N)) {
					openNodes.remove(N);
				}
				return X;
			}
		}
		
		public Node findTotalMinimumEstimatedCostNodeSoFar() {
			ListIterator<Node> itr = openNodes.listIterator();
			Node minNode = itr.next();
			float min = minNode.estimatedTotalCost;
			while (itr.hasNext()) {
				Node N = itr.next();
				if (N.estimatedTotalCost < min) {
					min = N.estimatedTotalCost;
					minNode = N;
				}
			}
			return minNode;
		}
		
		
		
		public ArrayList<Node> getPath(){
			ArrayList<Node> path = new ArrayList<Node>();
			//Collections.reverse(path);
			//return path;
			ListIterator<Node> itr = closedNodes.listIterator();
			Node N = NodeAry[goal];
			path.add(N);
			while (N.fromCon != null) {
				path.add(N.fromCon.fromNode);
				N = N.fromCon.fromNode;
			}
			return path;
		}
		
		public void showPath(boolean bot) {
			strokeWeight(4);
			for(int i=0;i<path.size()-1;i++){
				if(!bot)
					stroke(255, 0, 0);
				else
					stroke(0, 255, 0);
				line(path.get(i).x, path.get(i).y, path.get(i+1).x, path.get(i+1).y);
			}
		}
	}
	

	


	public void keyPressed(){
		//			if(keyPressed == true){
		if (key == CODED && character.location.x==destNode.x && character.location.y==destNode.y) {
			if (keyCode == LEFT && currentNode.left==true) {
				mouse = new PVector(currentNode.leftNode.x, currentNode.leftNode.y); 
				destNode = currentNode.leftNode;
			} else if (keyCode == RIGHT && currentNode.right==true) {
				mouse = new PVector(currentNode.rightNode.x, currentNode.rightNode.y);
				destNode = currentNode.rightNode;
			} else if (keyCode == UP && currentNode.top==true) {
				mouse = new PVector(currentNode.topNode.x, currentNode.topNode.y);
				destNode = currentNode.topNode;
			} else if (keyCode == DOWN && currentNode.bot==true) {
				mouse = new PVector(currentNode.botNode.x, currentNode.botNode.y);
				destNode = currentNode.botNode;
			}
		}
		if(key==' '){
			//if(hintcount<1){
				//playerPath.showPath(false);
				hintcount++;
			//}
		}
		if(key=='p'){
			textSize(36);
			text("PAUSED", (float)screenSize.getWidth()/2, (float)screenSize.getHeight()/2); 
			//System.out.println("-----");
			delay(2000);
			//System.out.println("-++-");
		}
		if(key=='c'){
			if(userChanged<2){
				createNodeArray();
				createNodes();
				updateNodeData();
				addHeuristics();
				conList = new ArrayList<Connection>();
				c.Prims();
				createConnection();
				printConnection();
				currentNode = NodeAry[currentNode.nodeNo];
				destNode = NodeAry[destNode.nodeNo];
				userChanged++;
			}
			
		}
	}

	class Character {
		PVector location;
		PVector target;
		PVector direction;
		PVector velocity;
		float orientation;
		PVector rotation;
		boolean bot;
		int length, height;
		PShape arrow;
		float maxVelocity = (float) 5;
		float timetoTarget = (float) 1;
		float acceleration = 10;

		Character(int x, int y, float speedx, float speedy, int length_, int height_,boolean bot) {
			location = new PVector(x, y);
			velocity = new PVector(speedx, speedy);
			length = length_;
			height = height_;
			target = new PVector(0, 0);
			direction = new PVector();
			this.bot = bot;
		}
		
		public void setOrientation(float trgtX, float trgtY) {
			this.orientation = atan2(trgtY - location.y, trgtX - location.x);
		}

		void update(SteeringOutput steer) {
			/*target = mouse;
			getNewOrientation(mouse);*/
			
			if (steer != null) {
				
			velocity.add(steer.linear);
				if(bot)
					velocity.limit((float) 14);
				else
					velocity.limit((float) 14);
				location.add(velocity);
			}

		}

		public void getNewOrientation(PVector location2) {
			float tempOrient;
			ofinal = PApplet.atan2((float) (target.y - location.y), (float) (target.x - location.x));
			tempOrient=ofinal;
			z = 0;
			//				}
		}

		void checkEdges() {
			if ((Math.abs(dist(location.x,location.y,target.x,target.y)) <= radiusofsat)) {// && Math.abs(target.y) <= radiusofsat

				location.x = mouse.x;
				location.y = mouse.y;
				velocity.x = 0;
				velocity.y = 0;

				z = 0;
				//System.out.println("bef"+currentNode.nodeNo);
				currentNode = destNode;
				//					System.out.println(key);
				//System.out.println("keyCode old == "+keyCode +","+keyPressed);
				//				if(keyPressed==true){
				if (keyCode == LEFT && currentNode.left==true) {
				//	System.out.println("keyCode == "+keyCode+","+keyPressed );
					mouse = new PVector(currentNode.leftNode.x, currentNode.leftNode.y); 
					destNode = currentNode.leftNode;
				}else if (keyCode == RIGHT && currentNode.right==true) {
					//System.out.println("keyCode == "+keyCode+","+keyPressed );
					mouse = new PVector(currentNode.rightNode.x, currentNode.rightNode.y); 
					destNode = currentNode.rightNode;
				}else if (keyCode == UP && currentNode.top==true) {
					//System.out.println("keyCode == "+keyCode+","+keyPressed );
					mouse = new PVector(currentNode.topNode.x, currentNode.topNode.y); 
					destNode = currentNode.topNode;
				}else if (keyCode == DOWN && currentNode.bot==true) {
					//System.out.println("keyCode == "+keyCode+","+keyPressed );
					mouse = new PVector(currentNode.botNode.x, currentNode.botNode.y); 
					destNode = currentNode.botNode;
				}
				//					System.out.println("keyCode == "+keyCode+","+keyPressed );
				//				}
			}
			if (Math.abs(target.x) <= radiusofdecel && Math.abs(target.y) <= radiusofdecel
					&& Math.abs(target.x) > radiusofsat && Math.abs(target.y) > radiusofsat) {

				target.sub(150, 150);

			}
		}

		public SteeringOutput getsteering() {
			SteeringOutput steer = new SteeringOutput();
			float targetSpeed;
			PVector targetVelocity;
			PVector direction = new PVector(target.x - location.x, target.y - location.y);
			float distance = direction.mag();

			if (distance < radiusofsat)
				return null;

			if (distance > radiusofdecel) {
				targetSpeed = maxVelocity;
			} else {
				targetSpeed = maxVelocity * (distance / radiusofdecel);
			}

			targetVelocity = direction;
			targetVelocity.normalize();
			targetVelocity = targetVelocity.mult(targetSpeed);
			PVector temp = targetVelocity.sub(velocity);
			temp = temp.div(timetoTarget);

			if (temp.mag() > acceleration) {
				temp = temp.normalize();
				temp = temp.mult(acceleration);
			}

			steer.setLinear(temp);
			steer.setAngular(0);

			return steer;
		}

		public float getheight() {
			// TODO Auto-generated method stub
			return height;
		}

		public float getlength() {
			// TODO Auto-generated method stub
			return length;
		}

		double getx() {
			return location.x;

		}

		double gety() {
			return location.y;

		}

	}

	public class SteeringOutput {
		PVector linear;
		float angular;

		public PVector getLinear() {
			return linear;
		}

		public void setLinear(PVector linear) {
			this.linear = linear;
		}

		public float getAngular() {
			return angular;
		}

		public void setAngular(float angular) {
			this.angular = angular;
		}
	}
	public class Arrive {
		Character myCharacter;
		float maxSpeed;
		float targetRadius;
		float timeToTarget;

		public Arrive(Character myCharacter, float maxS, float maxAcc, float trgtRad) {
			this.myCharacter = myCharacter;
			this.maxSpeed = maxS;
			if(myCharacter.bot)
				this.targetRadius = 7;
			else
				this.targetRadius = 7;
			this.timeToTarget = (float) 0.1;
		}

		public SteeringOutput getsteering() {
			SteeringOutput steer = new SteeringOutput();
			float targetSpeed;
			PVector targetVelocity;
			PVector direction = new PVector(myCharacter.target.x - myCharacter.location.x,
					myCharacter.target.y - myCharacter.location.y);
			float distance = direction.mag();

			if (distance < targetRadius) {
				if (myCharacter.bot) {
					myCharacter.velocity = new PVector(0, 0);

					if (i % 2 == 0) {
						try {
							if (pathIndex >= botPath.path.size()) {
								//pathIndex--;
								pathIndex = botPath.path.size()-1;
							}
							botPath.findPath(NodeAry[botPath.path.get(pathIndex).nodeNo], currentNode,
									botPath.path.get(pathIndex).nodeNo, currentNode.nodeNo);
							
						} catch (Exception e) {
							
							System.out.println(e.getLocalizedMessage());
						}

						pathIndex = 0;
					}
					pathIndex++;
					i++;
				}else{
					myCharacter.velocity=new PVector(0,0);
					playerPath.findPath(currentNode, NodeAry[Goal],currentNode.nodeNo,NodeAry.length-1);
					
					i++;
				}
				return null;
			}

			targetSpeed = maxSpeed;

			targetVelocity = direction;
			targetVelocity.normalize();
			targetVelocity = targetVelocity.mult(targetSpeed);
			// PVector temp = targetVelocity.sub(myCharacter.velocity);
			PVector temp = targetVelocity.div(timeToTarget);

			steer.setLinear(temp);
			steer.setAngular(0);

			return steer;
		}

		public void update() {
			// myCharacter.target=mouse;
			if (!myCharacter.location.equals(myCharacter.target))
				myCharacter.setOrientation(myCharacter.target.x, myCharacter.target.y);
			myCharacter.update(getsteering());
		}

		void checkEdges() {
			if ((Math.abs(dist(myCharacter.location.x, myCharacter.location.y, myCharacter.target.x,
					myCharacter.target.y)) <= this.targetRadius)) {// &&
																// Math.abs(target.y)
																// <=
																// radiusofsat

				myCharacter.location.x = mouse.x;
				myCharacter.location.y = mouse.y;
				myCharacter.velocity.x = 0;
				myCharacter.velocity.y = 0;

				z = 0;
				currentNode = destNode;
				if (keyCode == LEFT && currentNode.left == true) {
					mouse = new PVector(currentNode.leftNode.x, currentNode.leftNode.y);
					destNode = currentNode.leftNode;
				} else if (keyCode == RIGHT && currentNode.right == true) {
					mouse = new PVector(currentNode.rightNode.x, currentNode.rightNode.y);
					destNode = currentNode.rightNode;
				} else if (keyCode == UP && currentNode.top == true) {
					mouse = new PVector(currentNode.topNode.x, currentNode.topNode.y);
					destNode = currentNode.topNode;
				} else if (keyCode == DOWN && currentNode.bot == true) {
					mouse = new PVector(currentNode.botNode.x, currentNode.botNode.y);
					destNode = currentNode.botNode;
				}
			}
			if (Math.abs(myCharacter.target.x) <= radiusofdecel && Math.abs(myCharacter.target.y) <= radiusofdecel
					&& Math.abs(myCharacter.target.x) > radiusofsat && Math.abs(myCharacter.target.y) > radiusofsat) {

				myCharacter.target.sub(150, 150);

			}
		}
	}

}