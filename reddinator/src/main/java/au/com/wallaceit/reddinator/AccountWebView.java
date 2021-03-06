/*
 * Copyright 2013 Michael Boyde Wallace (http://wallaceit.com.au)
 * This file is part of Reddinator.
 *
 * Reddinator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Reddinator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Reddinator (COPYING). If not, see <http://www.gnu.org/licenses/>.
 */
package au.com.wallaceit.reddinator;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AccountWebView extends Activity {
    WebView wv;
    WebViewClient wvclient;
    Activity mActivity;
    GlobalObjects global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = ((GlobalObjects) AccountWebView.this.getApplicationContext());
        // request loading bar
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mActivity = AccountWebView.this;
        setContentView(R.layout.webview);
        mActivity.setTitle(R.string.loading);
        // set and load webview
        wv = (WebView) findViewById(R.id.webView);
        wvclient = new WebViewClient();
        wv.setWebViewClient(wvclient);
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mActivity.setProgress(progress * 100); //Make the bar disappear after URL is loaded
                // Return to the app name after loading
                if (progress == 100) {
                    mActivity.setTitle(R.string.app_name);
                }
            }
        });
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDefaultFontSize(22);
        // set session cookie
        String cookie = Uri.encode(global.mRedditData.getSessionCookie());
        System.out.println("CookieString: " + cookie);
        if (!cookie.equals("")) {
            CookieSyncManager.createInstance(wv.getContext());
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie(".reddit.com", "reddit_session=" + cookie);
            CookieSyncManager.getInstance().sync();
            CookieSyncManager.getInstance().startSync();
        }
        wv.loadUrl("http://www.reddit.com/message/inbox.compact");
    }

    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            wv.stopLoading();
            wv.loadData("", "text/html", "utf-8");
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                wv.stopLoading();
                wv.loadData("", "text/html", "utf-8");
                this.finish();
                return true;
        }
        return false;
    }

}

