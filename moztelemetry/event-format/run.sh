array_event_file="events_array.json"
obj_event_file="events_obj.json"

tee $array_event_file <<EOF
[
    [194010,"addonsManager","uninstall-other-gmp-widevinecdm",{"source":"gmp-plugin"}],
    [35026054, "security.ui.certerror", "load-aboutcerterror-SEC_ERROR_EXPIRED_CERTIFICATE", {"has_sts": "false", "is_frame": "true"}],
    [8051140, "addonsManager", "install-extension-1", {"step": "started", "addon_id": "fxmonitor@mozilla.org"}],
    [8179305, "addonsManager", "install-unknown-1", {"method": "amWebAPI", "step": "download_started", "source": "amo"}],
    [8180380, "addonsManager", "install-extension-1", {"download_time": "1075", "method": "amWebAPI", "step": "download_completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8180385, "addonsManager", "install-extension-1", {"method": "amWebAPI", "num_perms": "8", "num_origins": "13", "step": "permissions_prompt", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8181592, "addonsManager", "install-extension-1", {"method": "amWebAPI", "step": "completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8211432, "normandy", "enrollFailed-preference_rollout-pref-rollout-activity-stream-search-shortcuts-1507237", {"reason": "conflict", "preference": "browser.newtabpage.activity-stream.improvesearch.topSiteSearchShortcuts"}],
    [194010,"addonsManager","uninstall-other-gmp-widevinecdm",{"source":"gmp-plugin"}],
    [35026054, "security.ui.certerror", "load-aboutcerterror-SEC_ERROR_EXPIRED_CERTIFICATE", {"has_sts": "false", "is_frame": "true"}],
    [8051140, "addonsManager", "install-extension-1", {"step": "started", "addon_id": "fxmonitor@mozilla.org"}],
    [8179305, "addonsManager", "install-unknown-1", {"method": "amWebAPI", "step": "download_started", "source": "amo"}],
    [8180380, "addonsManager", "install-extension-1", {"download_time": "1075", "method": "amWebAPI", "step": "download_completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8180385, "addonsManager", "install-extension-1", {"method": "amWebAPI", "num_perms": "8", "num_origins": "13", "step": "permissions_prompt", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8181592, "addonsManager", "install-extension-1", {"method": "amWebAPI", "step": "completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}],
    [8211432, "normandy", "enrollFailed-preference_rollout-pref-rollout-activity-stream-search-shortcuts-1507237", {"reason": "conflict", "preference": "browser.newtabpage.activity-stream.improvesearch.topSiteSearchShortcuts"}]
]
EOF

tee $obj_event_file <<EOF
[
    {"timestamp": 194010, "category": "addonsManager", "event_name": "uninstall-other-gmp-widevinecdm", "extra": {"source":"gmp-plugin"}},
    {"timestamp": 35026054, "category": "security.ui.certerror", "event_name": "load-aboutcerterror-SEC_ERROR_EXPIRED_CERTIFICATE", "extra": {"has_sts": "false", "is_frame": "true"}},
    {"timestamp": 8051140, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"step": "started", "addon_id": "fxmonitor@mozilla.org"}},
    {"timestamp": 8179305, "category": "addonsManager", "event_name": "install-unknown-1", "extra": {"method": "amWebAPI", "step": "download_started", "source": "amo"}},
    {"timestamp": 8180380, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"download_time": "1075", "method": "amWebAPI", "step": "download_completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8180385, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"method": "amWebAPI", "num_perms": "8", "num_origins": "13", "step": "permissions_prompt", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8181592, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"method": "amWebAPI", "step": "completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8211432, "category": "normandy", "event_name": "enrollFailed-preference_rollout-pref-rollout-activity-stream-search-shortcuts-1507237", "extra": {"reason": "conflict", "preference": "browser.newtabpage.activity-stream.improvesearch.topSiteSearchShortcuts"}},
    {"timestamp": 194010, "category": "addonsManager", "event_name": "uninstall-other-gmp-widevinecdm", "extra": {"source":"gmp-plugin"}},
    {"timestamp": 35026054, "category": "security.ui.certerror", "event_name": "load-aboutcerterror-SEC_ERROR_EXPIRED_CERTIFICATE", "extra": {"has_sts": "false", "is_frame": "true"}},
    {"timestamp": 8051140, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"step": "started", "addon_id": "fxmonitor@mozilla.org"}},
    {"timestamp": 8179305, "category": "addonsManager", "event_name": "install-unknown-1", "extra": {"method": "amWebAPI", "step": "download_started", "source": "amo"}},
    {"timestamp": 8180380, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"download_time": "1075", "method": "amWebAPI", "step": "download_completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8180385, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"method": "amWebAPI", "num_perms": "8", "num_origins": "13", "step": "permissions_prompt", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8181592, "category": "addonsManager", "event_name": "install-extension-1", "extra": {"method": "amWebAPI", "step": "completed", "source": "amo", "addon_id": "jid1-NIfFY2CA8fy1tg@jetpack"}},
    {"timestamp": 8211432, "category": "normandy", "event_name": "enrollFailed-preference_rollout-pref-rollout-activity-stream-search-shortcuts-1507237", "extra": {"reason": "conflict", "preference": "browser.newtabpage.activity-stream.improvesearch.topSiteSearchShortcuts"}}
]
EOF

gzip -qf $array_event_file
gzip -qf $obj_event_file

array_size=$(wc -c <"$array_event_file.gz")
obj_size=$(wc -c <"$obj_event_file.gz")
percent_larger=$(echo "scale=2; 100 * ($obj_size - $array_size) / $obj_size" | bc)

echo "Object Representation (${obj_size}b) is $percent_larger% than Array Representation (${array_size}b)."
