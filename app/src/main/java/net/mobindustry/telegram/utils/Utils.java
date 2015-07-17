package net.mobindustry.telegram.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.mobindustry.telegram.R;
import net.mobindustry.telegram.core.ApiClient;
import net.mobindustry.telegram.core.handlers.BaseHandler;
import net.mobindustry.telegram.core.handlers.DownloadFileHandler;
import net.mobindustry.telegram.core.handlers.OkHandler;
import net.mobindustry.telegram.model.holder.DataHolder;
import net.mobindustry.telegram.model.holder.DownloadFileHolder;

import org.drinkless.td.libcore.telegram.TdApi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static SimpleDateFormat getDateFormat(String type) {
        return new SimpleDateFormat(type);
    }

    public static String getInitials(String firstName, String lastName) {
        if (firstName.isEmpty()) {
            return ":)";
        }
        if (lastName.isEmpty()) {
            char[] iconText = new char[2];
            firstName.getChars(0, 1, iconText, 0);
            firstName.getChars(1, 2, iconText, 1);
            return ("" + iconText[0] + iconText[1]).toUpperCase();
        } else {
            char[] iconText = new char[2];
            firstName.getChars(0, 1, iconText, 0);
            lastName.getChars(0, 1, iconText, 1);
            return ("" + iconText[0] + iconText[1]).toUpperCase();
        }
    }

    public static ShapeDrawable getShapeDrawable(int size, int color) {
        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.setIntrinsicHeight(size);
        circle.setIntrinsicWidth(size);
        circle.getPaint().setColor(color);
        return circle;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void photoFileCheckerAndLoader(final TdApi.File file, final ImageView view) {
        Log.e("Log", "File to string " + file.toString());

        if (file instanceof TdApi.FileLocal) {
            Log.e("Log", "FileLocal photo ok");
            TdApi.FileLocal fileLocal = (TdApi.FileLocal) file;
            ImageLoaderHelper.displayImage(Const.IMAGE_LOADER_PATH_PREFIX + fileLocal.path, view);
        } else if (file instanceof TdApi.FileEmpty) {
            Log.e("Log", "FileEmpty photo ok");
            final TdApi.FileEmpty fileEmpty = (TdApi.FileEmpty) file;
            if (DownloadFileHolder.getUpdatedFilePath(fileEmpty.id) != null) {
                ImageLoaderHelper.displayImage(String.valueOf(fileEmpty.id), view);
            } else {
                new ApiClient<>(new TdApi.DownloadFile(fileEmpty.id), new OkHandler(), new ApiClient.OnApiResultHandler() {
                    @Override
                    public void onApiResult(BaseHandler output) {
                        if (output.getHandlerId() == OkHandler.HANDLER_ID) {
                            ImageLoaderHelper.displayImage(String.valueOf(fileEmpty.id), view);
                        }
                    }
                }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
    }

    public static void gifFileCheckerAndLoader(final TdApi.File file, final ImageView view) {
        if (file instanceof TdApi.FileLocal) {
            Log.e("Log", "FileLocal gif ok");

            TdApi.FileLocal fileLocal = (TdApi.FileLocal) file;
            Glide.with(DataHolder.getContext()).load(fileLocal.path).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
        } else if (file instanceof TdApi.FileEmpty) {
            Log.e("Log", "FileEmpty gif ok");
            final TdApi.FileEmpty fileEmpty = (TdApi.FileEmpty) file;
            if (DownloadFileHolder.getUpdatedFilePath(fileEmpty.id) != null) {
                Glide.with(DataHolder.getContext()).load(DownloadFileHolder.getUpdatedFilePath(fileEmpty.id)).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
            } else {
                new ApiClient<>(new TdApi.DownloadFile(fileEmpty.id), new OkHandler(), new ApiClient.OnApiResultHandler() {
                    @Override
                    public void onApiResult(BaseHandler output) {
                        if (output.getHandlerId() == OkHandler.HANDLER_ID) {
                            String path = null;
                            for (int i = 0; i < 50; i++) {
                                path = DownloadFileHolder.getUpdatedFilePath(fileEmpty.id);
                                if (path != null) {
                                    break;
                                }
                                try {
                                    TimeUnit.MILLISECONDS.sleep(250);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Glide.with(DataHolder.getContext()).load(path).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);
                        }
                    }
                }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }
    }

    public static void photoFileLoader(final int id, final ImageView view) {
        new ApiClient<>(new TdApi.DownloadFile(id), new DownloadFileHandler(), new ApiClient.OnApiResultHandler() {
            @Override
            public void onApiResult(BaseHandler output) {
                if (output.getHandlerId() == DownloadFileHandler.HANDLER_ID) {
                    ImageLoaderHelper.displayImage(String.valueOf(id), view);
                }
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public static void hideKeyboard(EditText e) {
        InputMethodManager imm = (InputMethodManager) e.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
    }

    public static int compare(long lhs, long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    public static float getDensity(Resources res) {
        DisplayMetrics metrics = res.getDisplayMetrics();
        return metrics.density;
    }

    public static String getUserStatusString(TdApi.UserStatus status) {
        Context context = DataHolder.getContext();
        Locale.setDefault(Locale.US);
        String lastSeenString = "";

        switch (status.getConstructor()) {
            case TdApi.UserStatusOnline.CONSTRUCTOR:
                lastSeenString = context.getString(R.string.online);
                break;
            case TdApi.UserStatusOffline.CONSTRUCTOR:
                int ago = ((TdApi.UserStatusOffline) status).wasOnline;
                lastSeenString = context.getString(R.string.last_seen) + " " + DateUtils.getRelativeTimeSpanString((long) ago * 1000);
                break;
            case TdApi.UserStatusRecently.CONSTRUCTOR:
                lastSeenString = context.getString(R.string.ls_recently);
                break;
            case TdApi.UserStatusLastWeek.CONSTRUCTOR:
                lastSeenString = context.getString(R.string.ls_week_ago);
                break;
            case TdApi.UserStatusLastMonth.CONSTRUCTOR:
                lastSeenString = context.getString(R.string.ls_month_ago);
                break;
            default:
                break;
        }
        return lastSeenString;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double mByte = 1024.0;
        double k = size / mByte;
        double m = ((size / mByte) / mByte);
        double g = (((size / mByte) / mByte) / mByte);
        double t = ((((size / mByte) / mByte) / mByte) / mByte);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" B");
        }
        return hrSize;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void setIcon(TdApi.File file, int chatId, String firstName, String lastName, final ImageView iconImage, TextView icon) {
        if (file != null) {
            if (file.getConstructor() == TdApi.FileEmpty.CONSTRUCTOR) {
                final TdApi.FileEmpty fileEmpty = (TdApi.FileEmpty) file;
                if (fileEmpty.id != 0) {
                    new ApiClient<>(new TdApi.DownloadFile(fileEmpty.id), new DownloadFileHandler(), new ApiClient.OnApiResultHandler() {
                        @Override
                        public void onApiResult(BaseHandler output) {
                            if (output.getHandlerId() == DownloadFileHandler.HANDLER_ID) {
                                iconImage.setVisibility(View.VISIBLE);
                                ImageLoaderHelper.displayImageList(String.valueOf(fileEmpty.id), iconImage);
                            }
                        }
                    }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } else {
                    icon.setVisibility(View.VISIBLE);

                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        if(chatId < 0) {
                            icon.setBackgroundDrawable(Utils.getShapeDrawable(R.dimen.toolbar_icon_size, chatId));
                        } else {
                            icon.setBackgroundDrawable(Utils.getShapeDrawable(R.dimen.toolbar_icon_size, -chatId));
                        }
                    } else {
                        if(chatId < 0) {
                            icon.setBackground(Utils.getShapeDrawable(R.dimen.toolbar_icon_size, chatId));
                        } else {
                            icon.setBackground(Utils.getShapeDrawable(R.dimen.toolbar_icon_size, -chatId));
                        }
                    }
                    icon.setText(Utils.getInitials(firstName, lastName));
                }
            }
            if (file.getConstructor() == TdApi.FileLocal.CONSTRUCTOR) {
                iconImage.setVisibility(View.VISIBLE);
                TdApi.FileLocal fileLocal = (TdApi.FileLocal) file;
                ImageLoaderHelper.displayImageList(Const.IMAGE_LOADER_PATH_PREFIX + fileLocal.path, iconImage);
            }
        }
    }
}



