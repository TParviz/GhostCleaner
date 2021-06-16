package android.text.format;

import android.content.Context;
import android.content.res.Resources;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ConfigurationCompat;

import com.ghostcleaner.R;
import com.ghostcleaner.service.D;

import java.util.Locale;

/**
 * Utility class to aid in formatting common values that are not covered
 * by the {@link java.util.Formatter} class in {@link java.util}
 */
public final class MyFormatter {

    /**
     * {@hide}
     */
    public static final int FLAG_SHORTER = 1 << 0;
    /**
     * {@hide}
     */
    public static final int FLAG_CALCULATE_ROUNDED = 1 << 1;
    /**
     * {@hide}
     */
    public static final int FLAG_SI_UNITS = 1 << 2;
    /**
     * {@hide}
     */
    public static final int FLAG_IEC_UNITS = 1 << 3;

    /** {@hide} */
    public static class BytesResult {
        public final String value;
        public final String units;
        public final long roundedBytes;

        public BytesResult(String value, String units, long roundedBytes) {
            this.value = value;
            this.units = units;
            this.roundedBytes = roundedBytes;
        }
    }

    private static Locale localeFromContext(@NonNull Context context) {
        return ConfigurationCompat.getLocales(context.getResources().getConfiguration()).get(0);
    }

    /* Wraps the source string in bidi formatting characters in RTL locales */
    private static String bidiWrap(@NonNull Context context, String source) {
        final Locale locale = localeFromContext(context);
        if (TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL) {
            return BidiFormatter.getInstance(true /* RTL*/).unicodeWrap(source);
        } else {
            return source;
        }
    }

    /**
     * Formats a content size to be in the form of bytes, kilobytes, megabytes, etc.
     *
     * <p>As of O, the prefixes are used in their standard meanings in the SI system, so kB = 1000
     * bytes, MB = 1,000,000 bytes, etc.</p>
     *
     * <p class="note">In {@link android.os.Build.VERSION_CODES#N} and earlier, powers of 1024 are
     * used instead, with KB = 1024 bytes, MB = 1,048,576 bytes, etc.</p>
     *
     * <p>If the context has a right-to-left locale, the returned string is wrapped in bidi
     * formatting characters to make sure it's displayed correctly if inserted inside a
     * right-to-left string. (This is useful in cases where the unit strings, like "MB", are
     * left-to-right, but the locale is right-to-left.)</p>
     *
     * @param context Context to use to load the localized units
     * @param sizeBytes size value to be formatted, in bytes
     * @return formatted string with the number
     */
    public static String formatFileSize(@Nullable Context context, long sizeBytes) {
        return formatFileSize(context, sizeBytes, FLAG_IEC_UNITS);
    }

    /** @hide */
    public static String formatFileSize(@Nullable Context context, long sizeBytes, int flags) {
        if (context == null) {
            return "";
        }
        final BytesResult res = formatBytes(context.getResources(), sizeBytes, flags);
        return bidiWrap(context, context.getString(R.string.fileSizeSuffix,
            res.value, res.units));
    }

    /**
     * Like {@link #formatFileSize}, but trying to generate shorter numbers
     * (showing fewer digits of precision).
     */
    public static String formatShortFileSize(@Nullable Context context, long sizeBytes) {
        if (context == null) {
            return "";
        }
        final BytesResult res = formatBytes(context.getResources(), sizeBytes,
            FLAG_IEC_UNITS | FLAG_SHORTER);
        return bidiWrap(context, context.getString(R.string.fileSizeSuffix,
            res.value, res.units));
    }

    /** {@hide} */
    public static BytesResult formatBytes(Resources res, long sizeBytes, int flags) {
        final int unit = ((flags & FLAG_IEC_UNITS) != 0) ? 1024 : 1000;
        final boolean isNegative = (sizeBytes < 0);
        float result = isNegative ? -sizeBytes : sizeBytes;
        String units = D.INSTANCE.get("sizeB");
        long mult = 1;
        if (result > 900) {
            units = D.INSTANCE.get("sizeKb");
            mult = unit;
            result = result / unit;
        }
        if (result > 900) {
            units = D.INSTANCE.get("sizeMb");
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = D.INSTANCE.get("sizeGb");
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = D.INSTANCE.get("sizeTb");
            mult *= unit;
            result = result / unit;
        }
        if (result > 900) {
            units = D.INSTANCE.get("sizePb");
            mult *= unit;
            result = result / unit;
        }
        // Note we calculate the rounded long by ourselves, but still let String.format()
        // compute the rounded value. String.format("%f", 0.1) might not return "0.1" due to
        // floating point errors.
        final int roundFactor;
        final String roundFormat;
        if (mult == 1 || result >= 100) {
            roundFactor = 1;
            roundFormat = "%.0f";
        } else if (result < 1) {
            roundFactor = 100;
            roundFormat = "%.2f";
        } else if (result < 10) {
            if ((flags & FLAG_SHORTER) != 0) {
                roundFactor = 10;
                roundFormat = "%.1f";
            } else {
                roundFactor = 100;
                roundFormat = "%.2f";
            }
        } else { // 10 <= result < 100
            if ((flags & FLAG_SHORTER) != 0) {
                roundFactor = 1;
                roundFormat = "%.0f";
            } else {
                roundFactor = 100;
                roundFormat = "%.2f";
            }
        }

        if (isNegative) {
            result = -result;
        }
        final String roundedString = String.format(roundFormat, result);

        // Note this might overflow if abs(result) >= Long.MAX_VALUE / 100, but that's like 80PB so
        // it's okay (for now)...
        final long roundedBytes =
            (flags & FLAG_CALCULATE_ROUNDED) == 0 ? 0
                : (((long) Math.round(result * roundFactor)) * mult / roundFactor);

        return new BytesResult(roundedString, units, roundedBytes);
    }
}
