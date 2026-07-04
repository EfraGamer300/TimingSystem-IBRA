package me.makkuusen.timing.system.timetrial;

import me.makkuusen.timing.system.ApiUtilities;
import me.makkuusen.timing.system.DiscordUtils;
import me.makkuusen.timing.system.TimingSystem;
import me.makkuusen.timing.system.api.events.TimeTrialFinishEvent;
import me.makkuusen.timing.system.track.Track;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class RecordWebhookListener implements Listener {

    @EventHandler
    public void onTimeTrialFinish(TimeTrialFinishEvent event) {
        if (!TimingSystem.configuration.isDiscordRecordsEnabled()) return;

        String webhookUrl = TimingSystem.configuration.getDiscordWebhookUrl();
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        if (!event.isNewBestTime()) return;

        Player player = event.getPlayer();
        Track track = event.getTimeTrial().getTrack();

        List<TimeTrialFinish> top = track.getTimeTrials().getTopList(2);
        if (top.isEmpty()) return;

        TimeTrialFinish topFinish = top.get(0);
        if (topFinish.getPlayer() == null) return;
        if (!topFinish.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

        if (top.size() == 1 && event.getOldBestTime() != -1) return;

        String previousHolder = null;
        String previousTime = null;
        if (top.size() >= 2) {
            TimeTrialFinish prevRecord = top.get(1);
            if (prevRecord.getPlayer() != null) {
                previousHolder = prevRecord.getPlayer().getName();
                previousTime = ApiUtilities.formatAsTime(prevRecord.getTime());
            }
        }

        String newTime = ApiUtilities.formatAsTime(event.getTimeTrialFinish().getTime());
        String playerName = player.getName();
        String trackName = track.getDisplayName();
        String roleId = TimingSystem.configuration.getDiscordWebhookRoleId();

        DiscordUtils.sendRecordWebhook(webhookUrl, playerName, trackName, newTime, previousHolder, previousTime, roleId);
    }
}
