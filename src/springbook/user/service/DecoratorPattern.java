package springbook.user.service;


public class DecoratorPattern {

    public static void main(String[] args) {
        Display roadWithLaneWithTraffic = new TrafficDecorator(new LaneDecorator(new RoadDisplay()));
        roadWithLaneWithTraffic.draw();
    }
}

abstract class Display {
    public abstract void draw();
}


class RoadDisplay extends Display {

    @Override
    public void draw() {
        // TODO Auto-generated method stub
        System.out.println("기본 도로 표시");
    }

}

abstract class DisplayDecorator extends Display {
    private Display decoratedDisplay;

    public DisplayDecorator(Display decoratedDisplay) {
        this.decoratedDisplay = decoratedDisplay;
    }

    public void draw() {
        decoratedDisplay.draw();
    }
}

class LaneDecorator extends DisplayDecorator {

    public LaneDecorator(Display decoratedDisplay) {
        super(decoratedDisplay);
        // TODO Auto-generated constructor stub
    }

    public void draw() {
        super.draw();
        drawLane();
    }

    private void drawLane() {
        System.out.println("\t 차선 표시");
    }

}

class TrafficDecorator extends DisplayDecorator {

    public TrafficDecorator(Display decoratedDisplay) {
        super(decoratedDisplay);
        // TODO Auto-generated constructor stub
    }

    public void draw() {
        super.draw();
        drawTraffic();
    }

    private void drawTraffic() {
        System.out.println("\t 교통량 표시");
    }

}

