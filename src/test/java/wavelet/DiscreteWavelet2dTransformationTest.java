package wavelet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field; // このインポートを追加

import static org.junit.jupiter.api.Assertions.*;

// 新たに追加するimport文 (既存のテストコードに存在しない場合)
import java.awt.Point;
import java.awt.image.BufferedImage;
import utility.ImageUtility;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyString;


@DisplayName("DiscreteWavelet2dTransformation Class Unit Tests")
class DiscreteWavelet2dTransformationTest {

    // テスト用のダミーデータ (既存)
    private static final double[][] DUMMY_INPUT_2D = {
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0},
            {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0}
    };

    // DUMMY_INPUT_3D も内部の2D配列を16x16に調整。
    private static final double[][][] DUMMY_INPUT_3D = {
            { // Horizontal coefficients
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0}
            },
            { // Vertical coefficients
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0}
            },
            { // Diagonal coefficients
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0},
                {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0}
            }
    };

    // 新しいダミーデータ (16x16 のスケーリング係数と、それに対応する 3x16x16 のウェーブレット係数)
    private static final double[][] DUMMY_SCALING_16X16;
    static {
        DUMMY_SCALING_16X16 = new double[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                DUMMY_SCALING_16X16[i][j] = i * 16 + j + 1.0;
            }
        }
    }

    private static final double[][][] DUMMY_WAVELET_3X16X16;
    static {
        DUMMY_WAVELET_3X16X16 = new double[3][16][16];
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    DUMMY_WAVELET_3X16X16[k][i][j] = k * 256 + i * 16 + j + 10.0;
                }
            }
        }
    }

    // recomposedCoefficients フィールドにアクセスするためのヘルパーメソッド
    private double[][] getRecomposedCoefficientsFieldValue(DiscreteWavelet2dTransformation dwt2d) throws NoSuchFieldException, IllegalAccessException {
        Field field = DiscreteWavelet2dTransformation.class.getDeclaredField("recomposedCoefficients");
        field.setAccessible(true); // private/protected フィールドにアクセス可能にする
        return (double[][]) field.get(dwt2d);
    }

    @Test
    @DisplayName("Constructor with double[][] argument creates an instance")
    void testConstructorWith2DArray() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        assertNotNull(dwt2d, "DiscreteWavelet2dTransformation instance should not be null.");
        assertSame(DUMMY_INPUT_2D, dwt2d.sourceCoefficients(), "Source coefficients should be set correctly by constructor.");
    }

    @Test
    @DisplayName("Constructor with double[][] and double[][][] arguments creates an instance")
    void testConstructorWith2DAnd3DArrays() {
        // DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16 を使用
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16);
        assertNotNull(dwt2d, "DiscreteWavelet2dTransformation instance should not be null.");
        assertSame(DUMMY_SCALING_16X16, dwt2d.scalingCoefficients(), "Scaling coefficients should be set correctly by constructor.");
        assertSame(DUMMY_WAVELET_3X16X16, dwt2d.waveletCoefficients(), "Wavelet coefficients should be set correctly by constructor.");
    }

    @Test
    @DisplayName("applyTo() with valid double[][] argument returns itself and sets sourceCoefficients")
    void testApplyToWithValidDoubleArray() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        double[][] newData = new double[32][32]; // 32x32のダミーデータ
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                newData[i][j] = i * 32 + j;
            }
        }
        WaveletTransformation result = dwt2d.applyTo(newData);
        assertSame(dwt2d, result, "applyTo() should return the instance itself.");
        assertSame(newData, dwt2d.sourceCoefficients(), "applyTo() should set the new source coefficients.");
    }

    @Test
    @DisplayName("applyTo() throws IllegalArgumentException for non-double[][] object")
    void testApplyToThrowsIllegalArgumentExceptionForNon2DDoubleArray() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        Object invalidObject = new Object();
        assertThrows(IllegalArgumentException.class, () -> dwt2d.applyTo(invalidObject),
                "applyTo() should throw IllegalArgumentException for non-double[][] argument.");
    }

    @Test
    @DisplayName("applyTo() throws IllegalArgumentException for null argument")
    void testApplyToThrowsIllegalArgumentExceptionForNull() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        assertThrows(IllegalArgumentException.class, () -> dwt2d.applyTo(null),
                "applyTo() should throw IllegalArgumentException for null argument.");
    }

    @Test
    @DisplayName("transform() with valid DiscreteWavelet2dTransformation argument returns a new instance")
    void testTransformWithValidDiscreteWavelet2dTransformation() {
        DiscreteWavelet2dTransformation cwt = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        double[][] transformData = new double[32][32]; // 32x32のダミーデータ
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                transformData[i][j] = i * 32 + j + 100.0;
            }
        }
        DiscreteWavelet2dTransformation anotherDWT2d = new DiscreteWavelet2dTransformation(transformData);

        WaveletTransformation result = cwt.transform(anotherDWT2d);

        assertNotNull(result, "transform() should return a non-null instance.");
        assertNotSame(cwt, result, "transform() should return a new instance, not the original one.");
        assertTrue(result instanceof DiscreteWavelet2dTransformation, "transform() should return an instance of DiscreteWavelet2dTransformation.");
    }

    @Test
    @DisplayName("transform() throws IllegalArgumentException for invalid WaveletTransformation type")
    void testTransformThrowsIllegalArgumentExceptionForInvalidType() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        WaveletTransformation invalidTransformation = new DiscreteWavelet1dTransformation(new double[]{
            1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0
        });
        assertThrows(IllegalArgumentException.class, () -> dwt2d.transform(invalidTransformation),
                "transform() should throw IllegalArgumentException for invalid WaveletTransformation type.");
    }

    @Test
    @DisplayName("transform() throws IllegalArgumentException for null argument")
    void testTransformThrowsIllegalArgumentExceptionForNull() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        assertThrows(IllegalArgumentException.class, () -> dwt2d.transform(null),
                "transform() should throw IllegalArgumentException for null argument.");
    }

    @Test
    @DisplayName("sourceCoefficients() and sourceCoefficients(double[][]) work correctly")
    void testSourceCoefficientsGetterAndSetter() {
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_INPUT_2D);
        double[][] initialSource = dwt2d.sourceCoefficients();
        assertSame(DUMMY_INPUT_2D, initialSource, "Initial source coefficients should be the one set in constructor.");

        // scalingCoefficientsとrecomposedCoefficientsを計算させるために一度アクセス
        dwt2d.scalingCoefficients();
        dwt2d.recomposedCoefficients();
        assertNotNull(dwt2d.scalingCoefficients(), "Scaling coefficients should be computed after initial access.");
        assertNotNull(dwt2d.recomposedCoefficients(), "Recomposed coefficients should be computed after initial access.");

        double[][] newSource = new double[32][32]; // 32x32のダミーデータ
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                newSource[i][j] = i * 32 + j + 200.0;
            }
        }
        dwt2d.sourceCoefficients(newSource); // この呼び出しで scalingCoefficients と recomposedCoefficients が null にリセットされるはず

        assertSame(newSource, dwt2d.sourceCoefficients(), "Source coefficients should be updated by setter.");
        // recomposedCoefficients は null にリセットされることを確認
        try {
            assertNull(getRecomposedCoefficientsFieldValue(dwt2d), "Recomposed coefficients should be reset to null when sourceCoefficients is set.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access recomposedCoefficients field: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("scalingCoefficients() and scalingCoefficients(double[][]) work correctly")
    void testScalingCoefficientsGetterAndSetter() {
        // DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16 を使用
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16);
        double[][] initialScaling = dwt2d.scalingCoefficients();
        assertSame(DUMMY_SCALING_16X16, initialScaling, "Initial scaling coefficients should be the one set in constructor.");

        // recomposedCoefficients を計算させておく（後でnullにリセットされるかテストするため）
        dwt2d.recomposedCoefficients(); // この行は、recomposedCoefficients が計算されることを保証
        assertNotNull(dwt2d.recomposedCoefficients(), "Recomposed coefficients should be computed after initial access.");

        double[][] newScaling = new double[16][16]; // 16x16のダミーデータ
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                newScaling[i][j] = i * 16 + j + 300.0;
            }
        }
        dwt2d.scalingCoefficients(newScaling); // このセッターが recomposedCoefficients を null にする
        assertSame(newScaling, dwt2d.scalingCoefficients(), "Scaling coefficients should be updated by setter.");
        // ゲッターではなく、直接フィールドの値がnullになっているかを確認
        try {
            assertNull(getRecomposedCoefficientsFieldValue(dwt2d), "Recomposed coefficients should be reset to null when scalingCoefficients is set.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access recomposedCoefficients field: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("waveletCoefficients() and waveletCoefficients(double[][][]) work correctly")
    void testWaveletCoefficientsGetterAndSetter() {
        // DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16 を使用
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16, DUMMY_WAVELET_3X16X16);
        double[][][] initialWavelet = dwt2d.waveletCoefficients();
        assertSame(DUMMY_WAVELET_3X16X16, initialWavelet, "Initial wavelet coefficients should be the one set in constructor.");

        // recomposedCoefficients を計算させておく（後でnullにリセットされるかテストするため）
        dwt2d.recomposedCoefficients(); // この行は、recomposedCoefficients が計算されることを保証
        assertNotNull(dwt2d.recomposedCoefficients(), "Recomposed coefficients should be computed after initial access.");

        double[][][] newWavelet = new double[3][16][16]; // 3x16x16のダミーデータ
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    newWavelet[k][i][j] = k * 256 + i * 16 + j + 400.0;
                }
            }
        }
        dwt2d.waveletCoefficients(newWavelet); // このセッターが recomposedCoefficients を null にする
        assertSame(newWavelet, dwt2d.waveletCoefficients(), "Wavelet coefficients should be updated by setter.");
        // ゲッターではなく、直接フィールドの値がnullになっているかを確認
        try {
            assertNull(getRecomposedCoefficientsFieldValue(dwt2d), "Recomposed coefficients should be reset to null when waveletCoefficients is set.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access recomposedCoefficients field: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("diagonalWaveletCoefficients() returns correct array")
    void testDiagonalWaveletCoefficients() {
        // DUMMY_SCALING_16X16 を使用してコンストラクタを初期化
        double[][][] testWavelets = new double[3][16][16]; // 3x16x16のダミーデータ
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    testWavelets[k][i][j] = k * 256 + i * 16 + j + 500.0;
                }
            }
        }
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16); // scalingCoefficientsを設定
        dwt2d.waveletCoefficients(testWavelets); // setterを使って直接設定

        assertSame(testWavelets[2], dwt2d.diagonalWaveletCoefficients(), "Diagonal wavelet coefficients should return the correct array.");
    }

    @Test
    @DisplayName("horizontalWaveletCoefficients() returns correct array")
    void testHorizontalWaveletCoefficients() {
        // DUMMY_SCALING_16X16 を使用してコンストラクタを初期化
        double[][][] testWavelets = new double[3][16][16]; // 3x16x16のダミーデータ
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    testWavelets[k][i][j] = k * 256 + i * 16 + j + 600.0;
                }
            }
        }
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16); // scalingCoefficientsを設定
        dwt2d.waveletCoefficients(testWavelets); // setterを使って直接設定

        assertSame(testWavelets[0], dwt2d.horizontalWaveletCoefficients(), "Horizontal wavelet coefficients should return the correct array.");
    }

    @Test
    @DisplayName("verticalWaveletCoefficients() returns correct array")
    void testVerticalWaveletCoefficients() {
        // DUMMY_SCALING_16X16 を使用してコンストラクタを初期化
        double[][][] testWavelets = new double[3][16][16]; // 3x16x16のダミーデータ
        for (int k = 0; k < 3; k++) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    testWavelets[k][i][j] = k * 256 + i * 16 + j + 700.0;
                }
            }
        }
        DiscreteWavelet2dTransformation dwt2d = new DiscreteWavelet2dTransformation(DUMMY_SCALING_16X16); // scalingCoefficientsを設定
        dwt2d.waveletCoefficients(testWavelets); // setterを使って直接設定

        assertSame(testWavelets[1], dwt2d.verticalWaveletCoefficients(), "Vertical wavelet coefficients should return the correct array.");
    }
}