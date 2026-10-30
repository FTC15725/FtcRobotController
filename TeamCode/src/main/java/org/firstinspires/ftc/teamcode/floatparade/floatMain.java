package org.firstinspires.ftc.teamcode.floatparade;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Random;

@Autonomous           
public class floatMain extends OpMode {
    private static final String HEAD_SERVO = "head";

    // Anchor the twitches fall back to when the servo reports no pose at all, which is what a limp,
    // never-driven channel reports. Nothing is commanded at Init, so this is not a start pose: the
    // head stays wherever it is put by hand, and the twitches anchor on the pose the servo reports.
    // private static final double HEAD_FALLBACK_POSE = 0.86;
    private static final double HEAD_FALLBACK_POSE = 0.86;

    // How far the head twitches, in degrees: the one knob for the twitch size. A twitch flicks this
    // far one way, swings back double that, then comes home, so it always ends on the pose it
    // started from. Which way it flicks first is a coin flip.
    private static final double HEAD_TWITCH_DEGREES = 40.0;
    private static final double HEAD_SWING_BACK_DEGREES = HEAD_TWITCH_DEGREES * 2.0;

    // Servo travel that maps to the full 0.0 - 1.0 position range.
    // REV Smart Robot Servos default to 300 degrees; change this if yours is programmed differently.
    private static final double SERVO_TRAVEL_DEGREES = 300.0;

    // How fast the head snaps to each position, in degrees per second. High on purpose: each move
    // should be a quick flick, then the head sits still again.
    private static final double HEAD_SPEED_DPS = 240.0;

    // Each twitch starts this many seconds after the last one, picked at random every time.
    private static final double TWITCH_MIN_WAIT_SECONDS = 2.0;
    private static final double TWITCH_MAX_WAIT_SECONDS = 7.0;

    private static final double FLICK_SWING = HEAD_TWITCH_DEGREES / SERVO_TRAVEL_DEGREES;
    private static final double SWING_BACK = HEAD_SWING_BACK_DEGREES / SERVO_TRAVEL_DEGREES;

    public boolean isHeadRight = true;

    private Servo head;

    private final Random random = new Random();

    // Where this twitch is going: the flick, the doubled swing back across the anchor, and the pose
    // the twitch started from, which it returns to.
    private double flickTarget = HEAD_FALLBACK_POSE;
    private double swingTarget = HEAD_FALLBACK_POSE;
    private double returnTarget = HEAD_FALLBACK_POSE;

    // Pose the twitches are centered on: read from the servo when a twitch starts, never set at Init.
    private double anchorPose = HEAD_FALLBACK_POSE;

    // Whether that anchor came from the servo or had to fall back, for the Driver Station readout.
    private boolean anchorFromServo = false;

    // Set once this OpMode has actually driven the head itself; before that the SDK is only reporting
    // the channel state, not a pose.
    private boolean haveDrivenHead = false;

    private double lastTime = -1.0;
    private double waitTimer = 0.0;
    private double waitSeconds = TWITCH_MIN_WAIT_SECONDS;

    private int twitchCount = 0;
    private int clippedCount = 0;

    // HOLD = sitting still between twitches; FLICK = twitching out; SWING = doubled move back across
    // the anchor; RETURN = coming home to the pose the twitch started from.
    private enum HeadState { HOLD, FLICK, SWING, RETURN }

    private HeadState state = HeadState.HOLD;

    @Override
    public void init() {
        head = hardwareMap.get(Servo.class, HEAD_SERVO);

        // Nothing is commanded here on purpose. The servo channel is limp while the driver waits, so
        // the head can be centered by hand and stays exactly where it is put; the twitches anchor on
        // whatever pose the servo reports when they start.
        double reported = head.getPosition();
        boolean readable = !Double.isNaN(reported) && reported > 0.0;

        state = HeadState.HOLD;
        isHeadRight = true;
        lastTime = -1.0;
        waitTimer = 0.0;
        waitSeconds = nextWaitSeconds();
        twitchCount = 0;
        clippedCount = 0;
        anchorPose = HEAD_FALLBACK_POSE;
        anchorFromServo = false;
        haveDrivenHead = false;

        telemetry.addData("Head servo", HEAD_SERVO);
        if (readable) {
            telemetry.addData("Servo reports", "%.3f (%.0f deg)", reported, reported * SERVO_TRAVEL_DEGREES);
        } else {
            telemetry.addData("Servo reports", "nothing - twitches will anchor at %.3f", HEAD_FALLBACK_POSE);
        }
        telemetry.addData("Status", "Initialized. Center the head by hand, then press Start.");
        telemetry.update();
    }

    @Override
    public void start() {
        state = HeadState.HOLD;
        isHeadRight = true;
        lastTime = -1.0;
        waitTimer = 0.0;
        waitSeconds = nextWaitSeconds();
        twitchCount = 0;
        clippedCount = 0;
    }

    // Runs over and over until Stop is pressed or the auto period ends: sit still on the anchor
    // pose, wait a random 2 - 7 seconds, twitch out and back home, sit still again.
    @Override
    public void loop() {
        if (lastTime < 0.0) {
            lastTime = time;
        }
        double dt = time - lastTime;
        lastTime = time;

        switch (state) {
            case HOLD:
                // Nothing is commanded here; the servo keeps holding the pose the last move ended on.
                waitTimer += dt;
                if (waitTimer >= waitSeconds) {
                    beginTwitch();
                }
                break;

            case FLICK:
                if (moveToward(flickTarget, dt)) {
                    state = HeadState.SWING;
                }
                break;

            case SWING:
                if (moveToward(swingTarget, dt)) {
                    state = HeadState.RETURN;
                }
                break;

            case RETURN:
                if (moveToward(returnTarget, dt)) {
                    twitchCount++;
                    state = HeadState.HOLD;
                    waitTimer = 0.0;
                    waitSeconds = nextWaitSeconds();
                }
                break;
        }

        double pose = currentPose();

        telemetry.addData("Anchor", "%.3f (%.0f deg) from %s", anchorPose,
                anchorPose * SERVO_TRAVEL_DEGREES, anchorFromServo ? "servo" : "fallback");
        telemetry.addData("State", "%s", state);
        telemetry.addData("Twitch", "%s %.0f, back %.0f, home %.0f", isHeadRight ? "right" : "left",
                HEAD_TWITCH_DEGREES, HEAD_SWING_BACK_DEGREES, HEAD_TWITCH_DEGREES);
        telemetry.addData("Head", "%.3f (%.0f deg)", pose, pose * SERVO_TRAVEL_DEGREES);
        // The pause only exists once the twitch is done and the head is back home, so it is counted
        // down there and not while the head is still moving.
        if (state == HeadState.HOLD) {
            telemetry.addData("Pause left", "%.1f s", Math.max(0.0, waitSeconds - waitTimer));
        } else {
            telemetry.addData("Pause left", "twitching");
        }
        telemetry.addData("Twitches", twitchCount);
        telemetry.addData("Hit end of travel", clippedCount);
        telemetry.addData("Running for", "%.1f s", time);
        telemetry.update();
    }

    // Starts one twitch from wherever the head is sitting right now, so nothing is measured from a
    // fixed home pose: a coin flip picks which way it flicks first. The twitch finishes on that same
    // pose, so the anchor it uses never moves.
    private void beginTwitch() {
        anchorPose = currentPose();
        isHeadRight = random.nextBoolean();

        double flick = anchorPose + (isHeadRight ? FLICK_SWING : -FLICK_SWING);
        flickTarget = clamp(flick);

        // The swing is measured from where the flick ended, so it retraces the flick and carries the
        // twitch distance past the anchor pose.
        double swing = flickTarget + (isHeadRight ? -SWING_BACK : SWING_BACK);
        swingTarget = clamp(swing);

        // Then straight back to the anchor, so the head ends every twitch where it started it.
        returnTarget = anchorPose;

        if (flickTarget != flick || swingTarget != swing) {
            clippedCount++;
        }

        waitTimer = 0.0;
        state = HeadState.FLICK;
    }

    // Nudges the head toward target at HEAD_SPEED_DPS and reports true once it is there.
    private boolean moveToward(double target, double dt) {
        double current = currentPose();
        double step = HEAD_SPEED_DPS * dt / SERVO_TRAVEL_DEGREES;

        if (Math.abs(target - current) <= step) {
            // Only command the exact pose if we are not sitting on it already, so a move that is
            // entirely clipped by the end of travel does not fire a pointless command.
            if (target != current) {
                command(target);
            }
            return true;
        }

        command(clamp(current + Math.signum(target - current) * step));
        return false;
    }

    // The only place the head is ever commanded, which is what makes the channel drive the servo and
    // what makes the reported pose meaningful from then on.
    private void command(double pose) {
        double startPose = head.getPosition();
        double targetPose = pose;

        int steps = 50;
        long sleepTimeMs = 10;

        for (int i = 1; i <= steps; i++) {
            double intermediatePose = startPose + (targetPose - startPose) * ((double) i / steps);

            head.setPosition(intermediatePose);

            try {
                Thread.sleep(sleepTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        head.setPosition(pose);
        haveDrivenHead = true;
    }

    // Where the head is right now. Servo.getPosition() reports the last commanded position, not a
    // real reading of the horn. Until this OpMode has driven the head itself it is only reporting the
    // channel state, which tells us nothing while the channel is limp (NaN, or 0), so fall back.
    private double currentPose() {
        double pose = head.getPosition();
        if (Double.isNaN(pose) || (!haveDrivenHead && pose <= 0.0)) {
            anchorFromServo = false;
            return HEAD_FALLBACK_POSE;
        }
        anchorFromServo = true;
        return clamp(pose);
    }

    private static double clamp(double pose) {
        return Math.max(0.0, Math.min(1.0, pose));
    }

    // Random pause before the next twitch, somewhere between the two wait constants.
    private double nextWaitSeconds() {
        return TWITCH_MIN_WAIT_SECONDS
                + random.nextDouble() * (TWITCH_MAX_WAIT_SECONDS - TWITCH_MIN_WAIT_SECONDS);
    }
}
