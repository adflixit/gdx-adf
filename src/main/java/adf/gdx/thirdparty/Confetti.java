package adf.gdx.thirdparty;

/**
 * Falling confetti. Loosely optimized.
 * https://jsfiddle.net/hcxabsgh/
 */
public class Confetti {
  /*public float          X, Y, W, H;
  private ShapeRenderer rend;
  private int           mp;
  private float         angle;
  private float         tiltAngle;
  private boolean       confettiActive;
  private boolean       animationComplete;

  private float[]       parX;
  private float[]       parY;
  private float[]       parR;
  private float[]       parD;
  private float[]       parColorR;
  private float[]       parColorG;
  private float[]       parColorB;
  private float[]       parTilt;
  private float[]       parTiltAngleIncremental;
  private float[]       parTiltAngle;

  this.x = Math.random() * W; // x-coordinate
  this.y = (Math.random() * H) - H; //y-coordinate
  this.r = RandomFromTo(10, 30); //radius;
  this.d = (Math.random() * mp) + 10; //density;
  this.color = color;
  this.tilt = Math.floor(Math.random() * 10) - 10;
  this.tiltAngleIncremental = (Math.random() * 0.07) + .05;
  this.tiltAngle = 0;

  public Confetti(float x, float y, float w, float h, ShapeRenderer rnd, int max) {
    X = x;
    Y = y;
    W = w;
    H = h;
    rend = rnd;
    setMax(max);
  }

  public Confetti(float x, float y, float w, float h, ShapeRenderer rnd) {
    this(x, y, h, w, rnd, 150);
  }

  private void update() {
    var remainingFlakes = 0;
    var particle;
    angle += .01;
    tiltAngle += .1;

    for (int i=0; i < mp; i++) {
      particle = particles[i];
      if (animationComplete) {
        return;
      }

      if (!confettiActive && particle.y < -15) {
        particle.y = H + 100;
        continue;
      }

      // stepParticle
      particle.tiltAngle += particle.tiltAngleIncremental;
      parY[i] += (cosf(angle + parD[i]) + 3 + parR[i] / 2) / 2;
      parX[i] += sinf(angle);
      parTilt[i] = (sin(parTiltAng[i] - (i / 3.))) * 15;

      if (parY[i] <= H) {
        remainingFlakes++;
      }
      checkForReposition(particle, i);
    }

    if (remainingFlakes == 0) {
      StopConfetti();
    }
  }

  public void draw(Batch batch, float parentAlpha) {
    ctx.clearRect(0, 0, W, H);
    var results = [];
    for (var i = 0; i < mp; i++) {
        (function (j) {
            results.push(particles[j].draw());
        })(i);
    }
    Update();

    return results;
  }

  function InitializeConfetti() {
    particles = [];
    animationComplete = false;
    for (var i = 0; i < mp; i++) {
        var particleColor = particleColors.getColor();
        particles.push(new confettiParticle(particleColor));
    }
    StartConfetti();
  }

  void checkForReposition(int i) {
    if ((parX[i] > W + 20 || parX[i] < -20 || parY[i] > H) && confettiActive) {
      //66.67% of the flakes
      if (i % 5 > 0 || i % 2 == 0) {
        repositionParticle(i, randf(W), -10, floor(randf(10)) - 20);
      } else {
        if (sin(angle) > 0) {
          //Enter from the left
          repositionParticle(i, -20, randf(H), floor(randf(10)) - 20);
        } else {
          //Enter from the right
          repositionParticle(i, W + 20, randf(H), floor(randf(10)) - 20);
        }
      }
    }
  }

  void repositionParticle(int i, float xCoordinate, float yCoordinate, float tilt) {
    parX[i] = xCoordinate;
    parY[i] = yCoordinate;
    parTilt[i] = tilt;
  }

  function ClearTimers() {
    clearTimeout(reactivationTimerHandler);
    clearTimeout(animationHandler);
  }

  function deactivateConfetti() {
    confettiActive = false;
    ClearTimers();
  }

  function stopConfetti() {
    animationComplete = true;
    if (ctx == undefined) return;
    ctx.clearRect(0, 0, W, H);
  }

  function RestartConfetti() {
    ClearTimers();
    stopConfetti();
    reactivationTimerHandler = setTimeout(function () {
        confettiActive = true;
        animationComplete = false;
        InitializeConfetti();
    }, 100);
  }

  public Confetti setMax(int max) {
    mp = max;
    parX = new float[max];
    parY = new float[max];
    parR = new float[max];
    parD = new float[max];
    parColorR = new float[max];
    parColorG = new float[max];
    parColorB = new float[max];
    parTilt = new float[max];
    parTiltAngleIncremental = new float[max];
    parTiltAngle = new float[max];
    return this;
  }

  public Confetti move(float x, float y) {
    X = x;
    Y = y;
    return this;
  }

  public Confetti resize(float w, float h) {
    W = w;
    H = h;
    return this;
  }*/
}
