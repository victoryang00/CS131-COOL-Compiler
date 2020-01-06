import java.lang.StringBuilder;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;


public class BitSet //用于表示状态的集合
{
    private int BitSize; //状态数目
    private int AryLength;
    private int [] BitAry; //存储状态数据的整形数组(包含第i个状态则第i位为1否则为0)

    private int cleanBrush; //用于将int数组中多余位清零的清位刷子

    public BitSet(int size) throws Exception
    {
        if(size <= 0)
        {
            throw new Exception("Invalid index of \''" + size + "\'!");
        }
        this.BitSize = size;
        this.AryLength = (size-1) / 32 + 1;
        BitAry = new int[AryLength];
        //初始化
        for(int i = 0;i < AryLength;i++)
        {
            BitAry[i] = 0;
        }
        //构建清位刷子
        cleanBrush = -1;
        int zeroLen = (32 - size % 32) % 32;
        cleanBrush >>= zeroLen;
        cleanBrush <<= zeroLen;
    }

    //获取集合bit位数
    public int GetSize()
    {
        return BitSize;
    }

    //获取int[]形式的bit数组
    public int[] GetBitArray()
    {
        return BitAry.clone();
    }

    //检查第i位是否已被置位
    public boolean IsSet(int index) throws Exception
    {
        if(index < 0 || index >= BitSize)
        {
            throw new Exception("Invalid index of \''" + index + "\'!");
        }
        int AryPos = index / 32;
        int BitPos = 1 << (31 - index % 32);
        return (BitAry[AryPos] & BitPos) != 0;
    }

    //将第index位置为1
    public void Set(int index)  throws Exception
    {
        if(index < 0 || index >= BitSize)
        {
            throw new Exception("Invalid index of \''" + index + "\'!");
        }
        int AryPos = index / 32;
        int BitPos = 1 << (31 - index % 32);
        BitAry[AryPos] |= BitPos;
    }

    //将第index位置为0
    public void Clear(int index) throws Exception
    {
        if(index < 0 || index >= BitSize)
        {
            throw new Exception("Invalid index of \''" + index + "\'!");
        }
        int AryPos = index / 32;
        int BitPos = 1 << (31 - index % 32);
        BitPos = ~BitPos;
        BitAry[AryPos] &= BitPos;
    }

    //判断两个BitSet是否相等
    public boolean EqualTo(BitSet AnotherSet)
    {
        if(this.BitSize != AnotherSet.BitSize)
        {
            return false;
        }
        int[] AnotherAry = AnotherSet.GetBitArray();
        for(int i = 0; i < AryLength; i++)
        {
            if(BitAry[i] != AnotherAry[i])
            {
                return false;
            }
        }
        return true;
    }

    //检查是否有比特被置位
    public boolean IsAnySet()
    {
        int sum = 0;
        for(int i = 0; i < AryLength; i++)
        {
            sum += BitAry[i];
        }
        return sum != 0;
    }

    //与另一个BitSet的与操作
    public void And(BitSet AnotherSet)
    {
        int len = Math.min(AryLength, AnotherSet.AryLength);
        int [] AnotherAry = AnotherSet.GetBitArray();
        for(int i = 0;i < len; i++)
        {
            BitAry[i] &= AnotherAry[i];
        }
        cleanUseless();
    }

    //与另一个BitSet的或操作
    public void Or(BitSet AnotherSet)
    {
        int len = Math.min(AryLength, AnotherSet.AryLength);
        int [] AnotherAry = AnotherSet.GetBitArray();
        for(int i = 0;i < len; i++)
        {
            BitAry[i] |= AnotherAry[i];
        }
        cleanUseless();
    }

    //全部置为0
    public void ClearAll()
    {
        for(int i = 0; i < AryLength; i++)
        {
            BitAry[i] = 0;
        }
    }
    
    //全部置为1
    public void SetAll()
    {
        for(int i = 0; i < AryLength; i++)
        {
            BitAry[i] = -1;
        }
        cleanUseless();
    }

    //转为二进制格式的字符串
    public String ToBinaryString()
    {
        StringBuilder res = new StringBuilder(BitSize);
        int count = 0;
        for(int i = 0;i < AryLength; i++)
        {
            for(int j = 31;j >= 0 && count++ < BitSize;j--)
            {
                res.append((BitAry[i] >> j) & 1);
            }
        }
        return res.toString();
    }

    //清空int数组中分配的多余位上的数据
    private void cleanUseless()
    {
        BitAry[AryLength -1] &= cleanBrush;
    }
}
public class NFA2DFA
{
    private ArrayList<String> SignList; //符号列表
    
    private ArrayList<String> NFA_StateList; //NFA状态列表
    private int NFA_StartStateIndex; //NFA开始状态
    private ArrayList<Integer> NFA_EndList; //NFA结束状态列表
    private ArrayList<ArrayList<BitSet>> NFA_AdjacencyTable; //NFA邻接表

    private ArrayList<BitSet> DFA_StateList; //DFA状态列表
    private int DFA_StartState; //DFA开始状态
    private ArrayList<Integer> DFA_EndList; //DFA结束状态列表
    private ArrayList<ArrayList<Integer>> DFA_AdjacencyTable; //DFA邻接表

    public NFA2DFA(); //初始化
    public void ReadData(String FilePath) throws Exception; //从文件中读取数据
    public void Transition() throws Exception; //进行转换操作
    public void ShowResult() throws Exception; //输出结果

    private BitSet epsilonClosure(BitSet curState) throws Exception; //计算输入状态的epsilon闭包
    private BitSet move(BitSet curState, int signIndex) throws Exception; //从当前状态curState经过signIndex对应的符号所能到达的所有状态的集合
    private void clearData(); //清除所有数据
}
public NFA2DFA()
{
    //符号表
    SignList = new ArrayList<String>();
    //NFA部分
    NFA_StateList = new ArrayList<String>();
    NFA_StartStateIndex = 0;
    NFA_EndList = new ArrayList<Integer>();
    NFA_AdjacencyTable = new ArrayList<ArrayList<BitSet>>(); 
    //DFA部分
    DFA_StateList = new ArrayList<BitSet>();
    DFA_StartState = 0;
    DFA_EndList = new ArrayList<Integer>();
    DFA_AdjacencyTable = new ArrayList<ArrayList<Integer>>();
}
private void clearData()
{
    //符号表
    SignList.clear();
    //NFA部分
    NFA_StateList.clear();
    NFA_StartStateIndex = 0;
    NFA_EndList.clear();
    NFA_AdjacencyTable.clear();
    //DFA部分
    DFA_StateList.clear();
    DFA_StartState = 0;
    DFA_EndList.clear();
    DFA_AdjacencyTable.clear();
}
public void ReadData(String FilePath) throws Exception
{
    clearData(); //有可能是读入新数据，先清除就数据

    //打开文件输入流
    File NFAFile = new File(FilePath);
    if(!NFAFile.exists())
    {
        throw new Exception("Input file not found!");
    }
    
    FileReader NFAReader = new FileReader(NFAFile);
    Scanner NFAInput = new Scanner(NFAReader);

    //读入符号
    int SignCount = NFAInput.nextInt();
    for(int i = 0; i < SignCount;i++)
    {
        SignList.add(NFAInput.next());
    }
    //读入状态
    int StateCount = NFAInput.nextInt();
    for(int i = 0; i < StateCount;i++)
    {
        NFA_StateList.add(NFAInput.next());
    }
    //读入开始状态
    NFA_StartStateIndex = NFA_StateList.indexOf(NFAInput.next());
    //读入结束状态
    int EndCount = NFAInput.nextInt();
    for(int i = 0; i < EndCount; i++)
    {
        NFA_EndList.add(NFA_StateList.indexOf(NFAInput.next()));
    }
    //读入各边数据，构建邻接表
    for(int i = 0; i < StateCount; i++)
    {
        ArrayList<BitSet> curLine = new ArrayList<BitSet>();
        for(int j = 0;j <= SignCount; j++)
        {
            BitSet curSet = new BitSet(StateCount);
            String [] curState = NFAInput.next().split(",");
            for(String x : curState)
            {
                if(!x.equals("-1")) //状态集合不为空
                {
                    curSet.Set(NFA_StateList.indexOf(x));
                }
            }
            curLine.add(curSet);
        }
        NFA_AdjacencyTable.add(curLine);
    }

    NFAInput.close();
    NFAReader.close();
}
public void Transition() throws Exception
{
    if(NFA_StateList.size() == 0)
    {
        throw new Exception("Read data before starting Transition!");
    }

    //获取开始状态
    BitSet StartSet = epsilonClosure(NFA_AdjacencyTable.get(NFA_StartStateIndex).get(0));
    StartSet.Set(NFA_StartStateIndex);

    //设置开始状态
    DFA_StartState = 0;
    DFA_StateList.add(StartSet);

    BitSet curSet, newSet;
    int SignCount = SignList.size();
    boolean contain;
    int index;
    for(int i = 0; i < DFA_StateList.size(); i++) //遍历所有状态
    {
        DFA_AdjacencyTable.add(new ArrayList<Integer>());
        curSet = DFA_StateList.get(i);
        for(int j = 0; j < SignCount; j++) //依次处理各符号
        {
            newSet = epsilonClosure(move(curSet, j));
            if(!newSet.IsAnySet()) //当前状态转换集合为空
            {
                DFA_AdjacencyTable.get(i).add(-1);
                continue;
            }
            contain = false;
            index = 0;
            for(BitSet check : DFA_StateList) //检查所达集合是否存在
            {
                if(check.EqualTo(newSet))
                {
                    contain = true;
                    break;
                }
                index++;
            }
            if(!contain) //不存在则追加
            {
                DFA_StateList.add(newSet);
            }
            DFA_AdjacencyTable.get(i).add(index);
        }
    }
    
    //获取接受状态
    for(int i = 0; i < DFA_StateList.size(); i++)
    {
        BitSet state = DFA_StateList.get(i);
        for(int endIndex : NFA_EndList)
        {
            if(state.IsSet(endIndex))
            {
                DFA_EndList.add(i);
            }
        }
    }
}
private BitSet epsilonClosure(BitSet curState) throws Exception
{
    int size = curState.GetSize();
    BitSet result = new BitSet(size);
    result.Or(curState); //包含原状态集合
    BitSet mark = new BitSet(size); //处理标记
    boolean addNew = true;
    BitSet temp;
    
    while(addNew) //循环直到没有新状态加入
    {
        addNew = false;
        for(int i = size-1; i >= 0; i--)
        {
            if(result.IsSet(i) && !mark.IsSet(i))
            {
                mark.Set(i);
                temp = NFA_AdjacencyTable.get(i).get(0); //当前状态经epsilon转换可达的状态集合
                for(int j = size - 1; j >= 0; j--)
                {
                    if(temp.IsSet(j) && !result.IsSet(j)) //发现了新状态
                    {
                        result.Set(j);
                        addNew = true;
                    }
                }
            }
        }
    }
    return result;
}
private BitSet epsilonClosure(BitSet curState) throws Exception
{
    int size = curState.GetSize();
    BitSet result = new BitSet(size);
    result.Or(curState); //包含原状态集合
    BitSet mark = new BitSet(size); //处理标记
    boolean addNew = true;
    BitSet temp;
    
    while(addNew) //循环直到没有新状态加入
    {
        addNew = false;
        for(int i = size-1; i >= 0; i--)
        {
            if(result.IsSet(i) && !mark.IsSet(i))
            {
                mark.Set(i);
                temp = NFA_AdjacencyTable.get(i).get(0); //当前状态经epsilon转换可达的状态集合
                for(int j = size - 1; j >= 0; j--)
                {
                    if(temp.IsSet(j) && !result.IsSet(j)) //发现了新状态
                    {
                        result.Set(j);
                        addNew = true;
                    }
                }
            }
        }
    }
    return result;
}
//输出结果
public void ShowResult() throws Exception
{
    if(DFA_StateList.size() == 0)
    {
        throw new Exception("Start transition before displaying result!");
    }
    //输出DFA的所有状态集合
    System.out.println("DFA State List:");
    for(int i = 0; i < DFA_StateList.size();i++)
    {
        BitSet curSet = DFA_StateList.get(i);
        System.out.print("StateId: " + i + "\tContainedNFAState: ");
        if(!curSet.IsAnySet())
        {
            System.out.print(-1);
        }
        else
        {
            for(int j = 0; j < curSet.GetSize(); j++)
            {
                if(curSet.IsSet(j))
                {
                    System.out.print(NFA_StateList.get(j) + " ");
                }
            }
        }
        System.out.println();
    }

    //输出开始状态集合
    System.out.println("\nStart State: " + DFA_StartState);

    //输出终结状态集合
    System.out.print("End State List: ");
    for(int x : DFA_EndList)
    {
        System.out.print(x + " ");
    }
    System.out.println();

    //输出集合关系
    System.out.println("\nAdjacency Relation:");
    System.out.print("Id\t");
    for(String x : SignList)
    {
        System.out.print(x + "\t");
    }
    System.out.println();
    for(int i = 0; i < DFA_AdjacencyTable.size(); i++)
    {
        ArrayList<Integer> x = DFA_AdjacencyTable.get(i);
        System.out.print(i + "\t");
        for(int y : x)
        {
            System.out.print(y + "\t");
        }
        System.out.println();
    }
}
public static void main(String [] args) throws Exception
{
    NFA2DFA n2d = new NFA2DFA();
    n2d.ReadData("NFA.txt");
    n2d.Transition();
    n2d.ShowResult();
}