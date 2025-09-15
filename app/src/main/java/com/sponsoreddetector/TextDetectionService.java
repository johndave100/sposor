package com.sponsoreddetector;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

public class TextDetectionService extends AccessibilityService {
    private static final String TARGET_WORD = "sponsored";
    private boolean lastSponsoredState = false;
    private static final String TAG = "TextDetectionService";
    
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                boolean hasSponsoredText = searchForText(rootNode, TARGET_WORD);
                
                if (hasSponsoredText != lastSponsoredState) {
                    lastSponsoredState = hasSponsoredText;
                    
                    // Notify overlay service about the change
                    Intent intent = new Intent("com.sponsoreddetector.SPONSORED_DETECTED");
                    intent.putExtra("sponsored_found", hasSponsoredText);
                    sendBroadcast(intent);
                    Log.d(TAG, "Sponsored found: " + hasSponsoredText);
                }
                
                rootNode.recycle();
            }
        }
    }
    
    private boolean searchForText(AccessibilityNodeInfo node, String targetText) {
        if (node == null) return false;
        
        CharSequence text = node.getText();
        if (text != null && text.toString().toLowerCase().contains(targetText.toLowerCase())) {
            return true;
        }
        
        CharSequence contentDesc = node.getContentDescription();
        if (contentDesc != null && contentDesc.toString().toLowerCase().contains(targetText.toLowerCase())) {
            return true;
        }
        
        // Search in child nodes
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (searchForText(child, targetText)) {
                    child.recycle();
                    return true;
                }
                child.recycle();
            }
        }
        
        return false;
    }
    
    @Override
    public void onInterrupt() {
        // Handle interruption
    }
}