package lwjglview.graphics.animator;

import utils.math.Matrix;

public class AnimationQueue extends Animator {
	
	public AnimationQueue() {
		init();
	}
	
	public void push(Animator anim) {
		if(isEmpty()) {
			head = tail = new Link(anim);
			if(!paused) {
				anim.start();
			}
		}
		else {
			Link tmp = tail;
			tail = new Link(anim);
			tmp.next = tail;
		}
	}
	
	public Animator top() {
		return head.animation;
	}
	
	public Animator pop() {
		Animator ret = head.animation;
		head = head.next;
		if(head == null) {
			tail = null;
		}
		return ret;
	}
	
	public boolean isEmpty() {
		return head == null;
	}

	@Override
	protected Matrix calculateTransform() {
		Matrix ret = top().apply();
		if(!paused) {
			while(head != tail && top().isFinished()) {
				pop();
				top().start();
			}
		}
		return ret;
	}

	@Override
	public boolean isFinished() {
		return isEmpty() || head == tail && top().isFinished();
	}

	@Override
	public void start() {
		paused = false;
		if(!isEmpty()) {
			top().start();
		}
	}

	@Override
	public void pause() {
		paused = true;
		top().pause();
	}

	@Override
	public void resume() {
		paused = false;
		if(!isEmpty()) {
			top().resume();
		}
	}
	
	@Override
	public void finish() {
	}
	
	private void init() {
		head = tail = null;
		paused = true;
	}
	
	private class Link {
		public Link(Animator anim) {
			animation = anim;
			next = null;
		}
		public Animator animation;
		public Link next;
	}
	
	private Link head;
	private Link tail;
	private boolean paused;
	
}
