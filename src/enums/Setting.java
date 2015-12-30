/*
 * Copyright (c) 2015, Garrett Benoit. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package enums;

public enum Setting {
    GESTURE_CIRCLE_MIN_RADIUS(0.1, 5.0, 25.0, DecimalPrecision.ONE),
    GESTURE_CIRCLE_MIN_ARC(0.1, 1.5, 7.5, DecimalPrecision.ONE),
    GESTURE_SWIPE_MIN_LENGTH(15, 150, 750, DecimalPrecision.DEFAULT),
    GESTURE_SWIPE_MIN_VELOCITY(100, 1000, 5000, DecimalPrecision.DEFAULT),
    TOUCH_THRESHOLD(-0.20, -0.10, 0, DecimalPrecision.TWO);

    private final double minimumValue;
    private final double defaultValue;
    private final double maximumValue;
    private final DecimalPrecision decimalPrecision;
    
    private Setting(double minimumValue, double defaultValue, double maximumValue, DecimalPrecision decimalPrecision) {
        this.minimumValue = minimumValue;
        this.defaultValue = defaultValue;
        this.maximumValue = maximumValue;
        this.decimalPrecision = decimalPrecision;
    }
    
    public double getMin() {
        return minimumValue;
    }
    
    public double getDefault() {
        return defaultValue;
    }
    
    public double getMax() {
        return maximumValue;
    }
    
    public DecimalPrecision getDecimalPrecision() {
        return decimalPrecision;
    }
}
