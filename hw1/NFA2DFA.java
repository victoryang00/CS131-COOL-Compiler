import java.lang.StringBuilder;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;


public class BitSet //���ڱ�ʾ״̬�ļ���
{
    private int BitSize; //״̬��Ŀ
    private int AryLength;
    private int [] BitAry; //�洢״̬���ݵ���������(������i��״̬���iλΪ1����Ϊ0)

    private int cleanBrush; //���ڽ�int�����ж���λ�������λˢ��

    public BitSet(int size) throws Exception
    {
        if(size <= 0)
        {
            throw new Exception("Invalid index of \''" + size + "\'!");
        }
        this.BitSize = size;
        this.AryLength = (size-1) / 32 + 1;
        BitAry = new int[AryLength];
        //��ʼ��
        for(int i = 0;i < AryLength;i++)
        {
            BitAry[i] = 0;
        }
        //������λˢ��
        cleanBrush = -1;
        int zeroLen = (32 - size % 32) % 32;
        cleanBrush >>= zeroLen;
        cleanBrush <<= zeroLen;
    }

    //��ȡ����bitλ��
    public int GetSize()
    {
        return BitSize;
    }

    //��ȡint[]��ʽ��bit����
    public int[] GetBitArray()
    {
        return BitAry.clone();
    }

    //����iλ�Ƿ��ѱ���λ
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

    //����indexλ��Ϊ1
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

    //����indexλ��Ϊ0
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

    //�ж�����BitSet�Ƿ����
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

    //����Ƿ��б��ر���λ
    public boolean IsAnySet()
    {
        int sum = 0;
        for(int i = 0; i < AryLength; i++)
        {
            sum += BitAry[i];
        }
        return sum != 0;
    }

    //����һ��BitSet�������
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

    //����һ��BitSet�Ļ����
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

    //ȫ����Ϊ0
    public void ClearAll()
    {
        for(int i = 0; i < AryLength; i++)
        {
            BitAry[i] = 0;
        }
    }
    
    //ȫ����Ϊ1
    public void SetAll()
    {
        for(int i = 0; i < AryLength; i++)
        {
            BitAry[i] = -1;
        }
        cleanUseless();
    }

    //תΪ�����Ƹ�ʽ���ַ���
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

    //���int�����з���Ķ���λ�ϵ�����
    private void cleanUseless()
    {
        BitAry[AryLength -1] &= cleanBrush;
    }
}
public class NFA2DFA
{
    private ArrayList<String> SignList; //�����б�
    
    private ArrayList<String> NFA_StateList; //NFA״̬�б�
    private int NFA_StartStateIndex; //NFA��ʼ״̬
    private ArrayList<Integer> NFA_EndList; //NFA����״̬�б�
    private ArrayList<ArrayList<BitSet>> NFA_AdjacencyTable; //NFA�ڽӱ�

    private ArrayList<BitSet> DFA_StateList; //DFA״̬�б�
    private int DFA_StartState; //DFA��ʼ״̬
    private ArrayList<Integer> DFA_EndList; //DFA����״̬�б�
    private ArrayList<ArrayList<Integer>> DFA_AdjacencyTable; //DFA�ڽӱ�

    public NFA2DFA(); //��ʼ��
    public void ReadData(String FilePath) throws Exception; //���ļ��ж�ȡ����
    public void Transition() throws Exception; //����ת������
    public void ShowResult() throws Exception; //������

    private BitSet epsilonClosure(BitSet curState) throws Exception; //��������״̬��epsilon�հ�
    private BitSet move(BitSet curState, int signIndex) throws Exception; //�ӵ�ǰ״̬curState����signIndex��Ӧ�ķ������ܵ��������״̬�ļ���
    private void clearData(); //�����������
}
public NFA2DFA()
{
    //���ű�
    SignList = new ArrayList<String>();
    //NFA����
    NFA_StateList = new ArrayList<String>();
    NFA_StartStateIndex = 0;
    NFA_EndList = new ArrayList<Integer>();
    NFA_AdjacencyTable = new ArrayList<ArrayList<BitSet>>(); 
    //DFA����
    DFA_StateList = new ArrayList<BitSet>();
    DFA_StartState = 0;
    DFA_EndList = new ArrayList<Integer>();
    DFA_AdjacencyTable = new ArrayList<ArrayList<Integer>>();
}
private void clearData()
{
    //���ű�
    SignList.clear();
    //NFA����
    NFA_StateList.clear();
    NFA_StartStateIndex = 0;
    NFA_EndList.clear();
    NFA_AdjacencyTable.clear();
    //DFA����
    DFA_StateList.clear();
    DFA_StartState = 0;
    DFA_EndList.clear();
    DFA_AdjacencyTable.clear();
}
public void ReadData(String FilePath) throws Exception
{
    clearData(); //�п����Ƕ��������ݣ������������

    //���ļ�������
    File NFAFile = new File(FilePath);
    if(!NFAFile.exists())
    {
        throw new Exception("Input file not found!");
    }
    
    FileReader NFAReader = new FileReader(NFAFile);
    Scanner NFAInput = new Scanner(NFAReader);

    //�������
    int SignCount = NFAInput.nextInt();
    for(int i = 0; i < SignCount;i++)
    {
        SignList.add(NFAInput.next());
    }
    //����״̬
    int StateCount = NFAInput.nextInt();
    for(int i = 0; i < StateCount;i++)
    {
        NFA_StateList.add(NFAInput.next());
    }
    //���뿪ʼ״̬
    NFA_StartStateIndex = NFA_StateList.indexOf(NFAInput.next());
    //�������״̬
    int EndCount = NFAInput.nextInt();
    for(int i = 0; i < EndCount; i++)
    {
        NFA_EndList.add(NFA_StateList.indexOf(NFAInput.next()));
    }
    //����������ݣ������ڽӱ�
    for(int i = 0; i < StateCount; i++)
    {
        ArrayList<BitSet> curLine = new ArrayList<BitSet>();
        for(int j = 0;j <= SignCount; j++)
        {
            BitSet curSet = new BitSet(StateCount);
            String [] curState = NFAInput.next().split(",");
            for(String x : curState)
            {
                if(!x.equals("-1")) //״̬���ϲ�Ϊ��
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

    //��ȡ��ʼ״̬
    BitSet StartSet = epsilonClosure(NFA_AdjacencyTable.get(NFA_StartStateIndex).get(0));
    StartSet.Set(NFA_StartStateIndex);

    //���ÿ�ʼ״̬
    DFA_StartState = 0;
    DFA_StateList.add(StartSet);

    BitSet curSet, newSet;
    int SignCount = SignList.size();
    boolean contain;
    int index;
    for(int i = 0; i < DFA_StateList.size(); i++) //��������״̬
    {
        DFA_AdjacencyTable.add(new ArrayList<Integer>());
        curSet = DFA_StateList.get(i);
        for(int j = 0; j < SignCount; j++) //���δ��������
        {
            newSet = epsilonClosure(move(curSet, j));
            if(!newSet.IsAnySet()) //��ǰ״̬ת������Ϊ��
            {
                DFA_AdjacencyTable.get(i).add(-1);
                continue;
            }
            contain = false;
            index = 0;
            for(BitSet check : DFA_StateList) //������Ｏ���Ƿ����
            {
                if(check.EqualTo(newSet))
                {
                    contain = true;
                    break;
                }
                index++;
            }
            if(!contain) //��������׷��
            {
                DFA_StateList.add(newSet);
            }
            DFA_AdjacencyTable.get(i).add(index);
        }
    }
    
    //��ȡ����״̬
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
    result.Or(curState); //����ԭ״̬����
    BitSet mark = new BitSet(size); //������
    boolean addNew = true;
    BitSet temp;
    
    while(addNew) //ѭ��ֱ��û����״̬����
    {
        addNew = false;
        for(int i = size-1; i >= 0; i--)
        {
            if(result.IsSet(i) && !mark.IsSet(i))
            {
                mark.Set(i);
                temp = NFA_AdjacencyTable.get(i).get(0); //��ǰ״̬��epsilonת���ɴ��״̬����
                for(int j = size - 1; j >= 0; j--)
                {
                    if(temp.IsSet(j) && !result.IsSet(j)) //��������״̬
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
    result.Or(curState); //����ԭ״̬����
    BitSet mark = new BitSet(size); //������
    boolean addNew = true;
    BitSet temp;
    
    while(addNew) //ѭ��ֱ��û����״̬����
    {
        addNew = false;
        for(int i = size-1; i >= 0; i--)
        {
            if(result.IsSet(i) && !mark.IsSet(i))
            {
                mark.Set(i);
                temp = NFA_AdjacencyTable.get(i).get(0); //��ǰ״̬��epsilonת���ɴ��״̬����
                for(int j = size - 1; j >= 0; j--)
                {
                    if(temp.IsSet(j) && !result.IsSet(j)) //��������״̬
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
//������
public void ShowResult() throws Exception
{
    if(DFA_StateList.size() == 0)
    {
        throw new Exception("Start transition before displaying result!");
    }
    //���DFA������״̬����
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

    //�����ʼ״̬����
    System.out.println("\nStart State: " + DFA_StartState);

    //����ս�״̬����
    System.out.print("End State List: ");
    for(int x : DFA_EndList)
    {
        System.out.print(x + " ");
    }
    System.out.println();

    //������Ϲ�ϵ
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