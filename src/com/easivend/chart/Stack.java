package com.easivend.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.easivend.common.ToolClass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Stack extends Activity {
	private String title="";
	private double[] value=new double[]{17,1,5,34,12,33,6,7,15,20,22,23,24,37,5,14,20,26,7,45,16,20,4,15};
	private double maxvalue=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
//		String a=bundle.getString("title");
//		double b=bundle.getDouble("maxvalue");
//		double[] c=bundle.getDoubleArray("value");
//		ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<title="+a+",maxvalue="+b);
//		for(int i=0;i<24;i++)
//		{
//			ToolClass.Log(ToolClass.INFO,"EV_JNI","APP<<value["+i+"]="+c[i]);
//		}
		
		title=bundle.getString("title");
		maxvalue=bundle.getDouble("maxvalue");
		value=bundle.getDoubleArray("value");
		
		String[] titles = new String[] {title};
        List<double[]> values = new ArrayList<double[]>();
        values.add(value);
        //values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500, 11600, 13500 });
        //values.add(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
        int[] colors = new int[] { Color.CYAN};
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "月销售记录 ", "每一格15天", "销售", 0.5, 24, 0, maxvalue, Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        //renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        renderer.setXLabels(24);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        //这个是柱状图布局文件
        View view = ChartFactory.getBarChartView(this, buildBarDataset(titles, values), renderer, Type.DEFAULT); //Type.STACKED
        view.setBackgroundColor(Color.BLACK);
        //setContentView(view);
        
        //新建一个布局文件 ，往页面里面添加多个布局文件 
        final LinearLayout layout2 = new LinearLayout(this);
        layout2.setOrientation(LinearLayout.VERTICAL);
        Button bt1 = new Button(this);        
        bt1.setText("返回");          
        layout2.addView(bt1); //添加按钮
        layout2.addView(view);//添加柱状图
       
        setContentView(layout2);
        bt1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	 protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
	        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	        int length = titles.length;
	        for (int i = 0; i < length; i++) {
	            CategorySeries series = new CategorySeries(titles[i]);
	            double[] v = values.get(i);
	            int seriesLength = v.length;
	            for (int k = 0; k < seriesLength; k++) {
	                series.add(v[k]);
	            }
	            dataset.addSeries(series.toXYSeries());
	        }
	        return dataset;
	    }

	    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
	        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	        renderer.setAxisTitleTextSize(16);
	        renderer.setChartTitleTextSize(20);
	        renderer.setLabelsTextSize(15);
	        renderer.setLegendTextSize(15);
	        int length = colors.length;
	        for (int i = 0; i < length; i++) {
	            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	            r.setColor(colors[i]);
	            renderer.addSeriesRenderer(r);
	        }
	        return renderer;
	    }

	    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
	        renderer.setChartTitle(title);
	        renderer.setXTitle(xTitle);
	        renderer.setYTitle(yTitle);
	        renderer.setXAxisMin(xMin);
	        renderer.setXAxisMax(xMax);
	        renderer.setYAxisMin(yMin);
	        renderer.setYAxisMax(yMax);
	        renderer.setAxesColor(axesColor);
	        renderer.setLabelsColor(labelsColor);
	    }
	
}
