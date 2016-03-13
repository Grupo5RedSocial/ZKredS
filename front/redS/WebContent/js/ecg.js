
	Visor = function (y){
		this.currentPart=0;
		//this.y=new Array(cadena);
		this.baseline=201;
		this.total=0;
		this.temp=y.length;
		while(this.temp>0){
			this.temp=this.temp-2000;
			this.total++;
		}
		this.auto=0;
		//jg.setPrintable(true);

		this.draw = function draw(elemento, paginacion){
			this.jg = new jsGraphics(elemento);
			this.jg.setColor('black');
			this.jg.drawString('Lead II',40,5);
			this.jg.drawString('HR 80 per min',40,25);
			this.jg.drawString('Page '+(this.currentPart+1)+'/'+this.total,40,220);
			this.jg.setColor('blue');
			var x=0;
			this.jg.drawLine(x,y[0]+this.baseline,x,y[0]+this.baseline);
			for(i=1;i<y.length;i++){
			   x=x+0.5;
			   this.jg.drawLine(x-1,0-y[i-1]+this.baseline,x,0-y[i]+this.baseline);
			   if(x==1000) { break;}
			}
			this.jg.paint();
			//document.getElementById(paginacion).value=(this.currentPart+1)+'/'+this.total;
			return;
		}
		this.drawNext=function drawNext(){
			if(this.currentPart+1<this.total){
				this.currentPart++;
			}else{
				if(this.auto=1){
					this.currentPart=0;
				}else{
					return;
				}
			}
			this.jg.clear();
			this.jg.paint();
			start=this.currentPart*2000;
			var x=0;
			this.jg.setColor('black');
			this.jg.drawString('Lead II',40,5);
			this.jg.drawString('HR 80 per min',40,25);
			this.jg.drawString('Page '+(this.currentPart+1)+'/'+this.total,40,220);
			this.jg.setColor('blue');
			this.jg.drawLine(x,y[start]+this.baseline,x,y[start]+this.baseline);
			for(i=start;i<y.length;i++){
			   x=x+0.5;
			   this.jg.drawLine(x-1,0-y[i-1]+this.baseline,x,0-y[i]+this.baseline);
			   if(x==1000) { break;}
			}
			this.jg.paint();
			//document.getElementById(paginacion).value=(this.currentPart+1)+'/'+this.total;
			return;
		}
		this.drawPrev=function drawPrev(){
			if(this.currentPart>0){
				this.currentPart--;
			}else{
				return;
			}
			this.jg.clear();
			this.jg.paint();
			start=this.currentPart*2000;
			var x=0;
			this.jg.setColor('black');
			this.jg.drawString('Lead II',40,5);
			this.jg.drawString('HR 80 per min',40,25);
			this.jg.drawString('Page '+(this.currentPart+1)+'/'+this.total,40,220);
			this.jg.setColor('blue');
			this.jg.drawLine(x,y[start]+this.baseline,x,y[start]+this.baseline);
			for(i=start;i<y.length;i++){
			   x=x+0.5;
			   this.jg.drawLine(x-1,0-y[i-1]+this.baseline,x,0-y[i]+this.baseline);
			   if(x==1000) { break;}
			}
			this.jg.paint();
			//document.getElementById(paginacion).value=(this.currentPart+1)+'/'+this.total;
			return;
		}
		this.stopAuto=function stopAuto(){
			auto=0;
			document.images[2].src='auto.png';
			return;
		}
		this.startAuto=function startAuto(){
			if(auto==1) return stopAuto();
			auto=1;
			document.images[2].src='auto.gif';
			return autoRun();
		}
		this.autoRun=function autoRun(){
			if(auto==1) {
				drawNext();
				var timeoutID = setTimeout('autoRun()', 3000);
			}
		}
	}