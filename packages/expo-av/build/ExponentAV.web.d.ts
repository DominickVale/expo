import { PermissionResponse } from 'expo-modules-core';
import type { AVPlaybackNativeSource, AVPlaybackStatus, AVPlaybackStatusToSet } from './AV.types';
import type { RecordingStatus } from './Audio/Recording.types';
export declare const avWebAudioContext: AudioContext;
export interface AVMedia extends HTMLMediaElement {
    panner: StereoPannerNode;
    nodeSource: MediaElementAudioSourceNode;
}
declare const _default: {
    readonly name: string;
    getStatusForVideo(element: AVMedia): Promise<AVPlaybackStatus>;
    loadForVideo(element: AVMedia, nativeSource: AVPlaybackNativeSource, fullInitialStatus: AVPlaybackStatusToSet): Promise<AVPlaybackStatus>;
    unloadForVideo(element: AVMedia): Promise<AVPlaybackStatus>;
    setStatusForVideo(element: AVMedia, status: AVPlaybackStatusToSet): Promise<AVPlaybackStatus>;
    replayVideo(element: AVMedia, status: AVPlaybackStatusToSet): Promise<AVPlaybackStatus>;
    setAudioMode(): Promise<void>;
    setAudioIsEnabled(): Promise<void>;
    getStatusForSound(element: AVMedia): Promise<AVPlaybackStatus>;
    loadForSound(nativeSource: string | {
        [key: string]: any;
        uri: string;
    }, fullInitialStatus: AVPlaybackStatusToSet): Promise<[AVMedia, AVPlaybackStatus]>;
    unloadForSound(element: AVMedia): Promise<AVPlaybackStatus>;
    setStatusForSound(element: AVMedia, status: AVPlaybackStatusToSet): Promise<AVPlaybackStatus>;
    replaySound(element: AVMedia, status: AVPlaybackStatusToSet): Promise<AVPlaybackStatus>;
    getAudioRecordingStatus(): Promise<RecordingStatus>;
    prepareAudioRecorder(options: any): Promise<{
        uri: string | null;
        status: Pick<RecordingStatus, Exclude<keyof RecordingStatus, 'canRecord'>>;
    }>;
    startAudioRecording(): Promise<RecordingStatus>;
    pauseAudioRecording(): Promise<RecordingStatus>;
    stopAudioRecording(): Promise<RecordingStatus>;
    unloadAudioRecorder(): Promise<void>;
    getPermissionsAsync(): Promise<PermissionResponse>;
    requestPermissionsAsync(): Promise<PermissionResponse>;
};
export default _default;
